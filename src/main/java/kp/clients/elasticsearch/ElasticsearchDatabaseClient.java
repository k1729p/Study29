package kp.clients.elasticsearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.mapping.Property;
import co.elastic.clients.elasticsearch._types.mapping.TypeMapping;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.indices.GetIndexResponse;
import co.elastic.clients.elasticsearch.indices.IndexState;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import kp.domain.company.Department;
import kp.domain.company.Employee;
import kp.domain.company.Title;
import kp.utils.Tools;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Elasticsearch client.
 */
public class ElasticsearchDatabaseClient {
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final String INDEX_DEPARTMENTS = "departments";
    private static final String INDEX_EMPLOYEES = "employees";

    /**
     * Processes the queries.
     *
     * @param key the key
     */
    public static void process(String key) {
        try (RestClient restClient = createRestClient()) {
            final ElasticsearchTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());
            final ElasticsearchClient client = new ElasticsearchClient(transport);

            switch (key) {
                case "ELA_01" -> discoverSchema(client);
                case "ELA_02" -> getDepartmentsAndEmployees(client);
                default -> logger.warn("process(): unhandled key[{}]", key);
            }
        } catch (Exception e) {
            logger.error("process(): Exception[{}]", e.getMessage());
            throw new RuntimeException(e);
        }
        logger.info("process(): key[{}]", key);
    }

    /**
     * Discovers schemas (indices and their property mappings) in the Elasticsearch database.
     *
     * @param client the Elasticsearch client
     */
    private static void discoverSchema(ElasticsearchClient client) {

        logger.info("### Discover indices and their property mappings ###");
        try {
            final GetIndexResponse response = client.indices().get(bld -> bld.index("*"));
            final Consumer<Map<String, Property>> consumer = map -> map
                    .forEach((propName, prop) ->
                            logger.info("\tProperty name[{}], type[{}]", propName, prop._kind()));
            response.indices().forEach((indexName, indexState) -> {
                logger.info("Index name[{}]", indexName);
                Optional.ofNullable(indexState).map(IndexState::mappings)
                        .map(TypeMapping::properties).ifPresent(consumer);
            });
        } catch (IOException e) {
            logger.error("discoverSchema(): IOException[{}]", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * Queries departments and their employees using standard generic Maps to avoid type definition issues.
     *
     * @param client the Elasticsearch client
     */
    private static void getDepartmentsAndEmployees(ElasticsearchClient client) {
        logger.info("### Departments and Employees ###");
        SearchResponse<Object> departmentResponse;
        SearchResponse<Object> employeeResponse;
        try {
            departmentResponse = client.search(bld -> bld
                    .index(INDEX_DEPARTMENTS)
                    .size(1000), Object.class);
            employeeResponse = client.search(bld -> bld
                    .index(INDEX_EMPLOYEES)
                    .size(1000), Object.class);
        } catch (IOException e) {
            logger.error("getDepartmentsAndEmployees(): IOException[{}]", e.getMessage());
            throw new RuntimeException(e);
        }
        final List<Department> departmentList = loadsDepartmentsAndEmployees(departmentResponse, employeeResponse);
        printDepartments(departmentList);
    }

    /**
     * Loads departments and employees.
     *
     * @param departmentResponse the department response
     * @param employeeResponse   the employee response
     * @return the department list
     */
    private static List<Department> loadsDepartmentsAndEmployees(
            SearchResponse<Object> departmentResponse, SearchResponse<Object> employeeResponse) {

        final Function<Object, Integer> intFun = (object) -> Optional.ofNullable(object)
                .filter(Integer.class::isInstance).map(Integer.class::cast).orElse(0);
        final Function<Object, String> strFun = (object) -> Optional.ofNullable(object)
                .filter(String.class::isInstance).map(String.class::cast).orElse("");
        final Map<Integer, String> departmentNames = new LinkedHashMap<>();
        final Map<Integer, List<Employee>> departmentEmployees = new LinkedHashMap<>();

        departmentResponse.hits().hits().forEach(hit -> Optional.ofNullable(hit.source())
                .filter(Map.class::isInstance).map(Map.class::cast)
                .ifPresent(source -> {
                    final int id = intFun.apply(source.get("id"));
                    final String name = strFun.apply(source.get("name"));
                    departmentNames.putIfAbsent(id, name);
                    departmentEmployees.putIfAbsent(id, new ArrayList<>());
                }));
        employeeResponse.hits().hits().forEach(hit -> Optional.ofNullable(hit.source())
                .filter(Map.class::isInstance).map(Map.class::cast)
                .ifPresent(source -> {
                    final int departmentId = intFun.apply(source.get("departmentId"));
                    if (!departmentEmployees.containsKey(departmentId)) {
                        return;
                    }
                    final int id = intFun.apply(source.get("id"));
                    final String firstName = strFun.apply(source.get("firstName"));
                    final String lastName = strFun.apply(source.get("lastName"));
                    final Title title = Title.fromString(strFun.apply(source.get("title")));
                    departmentEmployees.get(departmentId).add(new Employee(id, firstName, lastName, title));
                }));
        return departmentNames.entrySet().stream()
                .map(entry ->
                        new Department(entry.getKey(), entry.getValue(), departmentEmployees.get(entry.getKey())))
                .toList();
    }

    /**
     * Prints departments to standard logging.
     *
     * @param departments the departments
     */
    private static void printDepartments(List<Department> departments) {
        logger.info("- ".repeat(20));
        departments.forEach(department -> {
            logger.info("department id[{}]", department.id());
            logger.info("department name[{}]", department.name());
            department.employees().forEach(employee -> {
                logger.info("\t employee id[{}]", employee.id());
                logger.info("\t employee name[{} {}]", employee.firstName(), employee.lastName());
                logger.info("\t employee title[{}]", employee.title());
            });
            logger.info("- ".repeat(20));
        });
    }

    /**
     * Creates the underlying HTTP RestClient for Elasticsearch.
     *
     * @return the RestClient
     */
    private static RestClient createRestClient() {

        final String host = Tools.getEnvOrDefault("ELASTICSEARCH_HOST", "localhost");
        final int port = Integer.parseInt(Tools.getEnvOrDefault("ELASTICSEARCH_PORT", "9200"));
        return RestClient.builder(new HttpHost(host, port, "http")).build();
    }
}