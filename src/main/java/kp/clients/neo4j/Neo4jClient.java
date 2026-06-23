package kp.clients.neo4j;

import kp.domain.company.Department;
import kp.domain.northwind.*;
import kp.utils.DatasetTools;
import kp.utils.Tools;
import org.neo4j.driver.*;
import org.neo4j.driver.Record;
import org.neo4j.driver.types.Node;
import org.slf4j.LoggerFactory;
import tools.jackson.databind.ObjectMapper;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

import static kp.clients.neo4j.Neo4jConstants.*;

/**
 * Neo4j client.
 * <p>
 * The Driver object is actually the Connection Pool. Creating it is heavy.
 * The driver should be created once when application starts and closed when application shuts down.
 * </p>
 */
public class Neo4jClient {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     * Processes Cypher queries with transaction.
     *
     * @param key the key
     */
    public static void process(String key) {

        Logger.getLogger("org.neo4j.driver").setLevel(Level.WARNING);
        try (Driver driver = createConnectionPool()) {
            driver.verifyConnectivity();

            switch (key) {
                case "NEO_01":
                    executeCypherQuery(driver, SCHEMA_DISCOVERY_QUERIES);
                    break;
                case "NEO_02":
                    DatasetTools.recreateDepartmentsDatasetInNeo4j(driver);
                    final List<Department> departmentList = getDepartments(driver);
                    Tools.printDepartments(departmentList);
                    executeCypherQuery(driver, List.of(DEPARTMENTS_AND_EMPLOYEES_QUERY));
                    break;
                case "NEO_03":
                    executeCypherQuery(driver, NORTHWIND_INITIALIZATION_QUERIES_1);
                    executeCypherQuery(driver, NORTHWIND_INITIALIZATION_QUERIES_2);
                    break;
                case "NEO_04":
                    executeCypherQuery(driver, NORTHWIND_READ_ALL_QUERIES);
                    break;
                case "NEO_05":
                    executeCypherQuery(driver, CYPHER_QUERIES);
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            logger.error("process(): exception[{}]", e.getMessage());
            throw new RuntimeException(e);
        }
    }
    /**
     * Gets departments and employees
     *
     * @param driver the driver for Neo4j server
     * @return the list with departments
     */
    private static List<Department> getDepartments(Driver driver) {

        final List<Record> recordList = new ArrayList<>();
        final TransactionCallback<Result> transactionCallback = transactionContext -> {
            recordList.addAll(transactionContext.run(DEPARTMENTS_AND_EMPLOYEES_QUERY).list());
            return null;
        };
        try (Session session = driver.session()) {
            session.executeWrite(transactionCallback);
        } catch (Exception e) {
            logger.error("getDepartments(): exception[{}]", e.getMessage());
            throw new RuntimeException(e);
        }
        return recordList.stream().map(record -> {
            final List<kp.domain.company.Employee> employees = record.get("employees")
                    .asList(Value::asNode).stream()
                    .map(empNode ->
                            new kp.domain.company.Employee(
                                    (int) empNode.get("id").asLong(),
                                    empNode.get("firstName").asString(),
                                    empNode.get("lastName").asString(),
                                    kp.domain.company.Title.fromString(empNode.get("title").asString(""))))
                    .toList();
            final Node deptNode = record.get("department").asNode();
            return new kp.domain.company.Department(
                    (int) deptNode.get("id").asLong(), deptNode.get("name").asString(), employees);
        }).toList();
    }

    /**
     * Executes Cypher query.
     *
     * @param driver    the driver for Neo4j server
     * @param queryList the list with Cypher queries
     */
    private static void executeCypherQuery(Driver driver, List<String> queryList) {

        final List<List<Record>> recordList = executeTransaction(driver, queryList);
        for (int i = 0; i < queryList.size(); i++) {
            System.out.printf("%s%n%d. query:%n%s%n", "- ".repeat(50), i + 1, queryList.get(i));
            System.out.printf("%d. record list:%n", i + 1);
            recordList.get(i).forEach(record -> record.fields().forEach(pair ->
                    System.out.printf("   key[%s], value[%s]%n", pair.key(), pair.value())));
        }
        System.out.println("- ".repeat(50));
    }

    /**
     * Executes a unit of work as a single, managed transaction.
     *
     * @param driver    the driver for Neo4j server
     * @param queryList the list with Cypher queries
     * @return the record list
     */
    private static List<List<Record>> executeTransaction(Driver driver, List<String> queryList) {

        final List<List<Record>> recordList = new ArrayList<>();
        final TransactionCallback<Result> transactionCallback = transactionContext -> {
            for (String query : queryList) {
                recordList.add(transactionContext.run(query).list());
            }
            return null;
        };
        try (Session session = driver.session()) {
            session.executeWrite(transactionCallback);
        } catch (Exception e) {
            logger.error("executeTransaction(): exception[{}]", e.getMessage());
            throw new RuntimeException(e);
        }
        return recordList;
    }

    /**
     * Processes queries for Web.
     *
     * @param selector the query selector
     * @return the JSON response
     */
    public static String processForWeb(int selector) {

        Logger.getLogger("org.neo4j.driver").setLevel(Level.WARNING);
        final String queryText;
        Map<String, Object> paramsMap = Map.of();
        switch (selector) {
            case 1:
                queryText = API_READY_QUERY_01;
                paramsMap = Map.of("id", 10643);
                break;
            case 2:
                queryText = API_READY_QUERY_02;
                break;
            case 3:
                queryText = API_READY_QUERY_03;
                break;
            default:
                return "";
        }
        final EagerResult eagerResult;
        try (Driver driver = createConnectionPool()) {
            eagerResult = driver.executableQuery(queryText).withParameters(paramsMap).execute();
        } catch (Exception e) {
            logger.error("processForWeb(): exception[{}]", e.getMessage());
            throw new RuntimeException(e);
        }
        return Optional.ofNullable(eagerResult)
                .map(EagerResult::records)
                .filter(Predicate.not(List::isEmpty))
                .map(List::getFirst)
                .filter(record -> record.containsKey(JSON_RESPONSE_KEY))
                .map(record -> record.get(JSON_RESPONSE_KEY))
                .map(Value::asList)
                .map(OBJECT_MAPPER::writeValueAsString)
                .orElse("");
    }

    /**
     * Reads customers.
     *
     * @return the customer list
     */
    public static List<Customer> getCustomers() {

        final List<Customer> customerList;
        try (Driver driver = createConnectionPool()) {
            driver.verifyConnectivity();
            customerList = driver.executableQuery(GET_CUSTOMERS_QUERY)
                    .withParameters(GET_CUSTOMERS_QUERY_PARAMETERS)
                    .execute().records().stream()
                    .map(record -> record.get("customerTree").as(Customer.class))
                    .toList();
        } catch (Exception e) {
            logger.error("getCustomers(): exception[{}]", e.getMessage());
            throw new RuntimeException(e);
        }
        customerList.forEach(Neo4jClient::printCustomer);
        System.out.println("- ".repeat(50));
        return customerList;
    }

    /**
     * Logs customer.
     *
     * @param customer the customer
     */
    private static void printCustomer(Customer customer) {

        System.out.printf("customerID[%s], purchasedOrders size[%d]%n",
                customer.customerID(), customer.purchasedOrders().size());
        Optional.of(customer.purchasedOrders()).stream().flatMap(List::stream)
                .filter(ord -> !ord.orderedProducts().isEmpty())
                .flatMap(ord -> ord.orderedProducts().stream())
                .forEach(orderItem -> {
                    final Product prd = orderItem.product();
                    System.out.printf("\tproductId[%d], quantity[%s], partOfCategories size[%d]%n",
                            prd.productID(), orderItem.quantity(), prd.partOfCategories().size());
                    Optional.of(prd.partOfCategories()).stream().flatMap(List::stream)
                            .forEach(cat -> System.out.printf("\t\tcategoryID[%d]%n", cat.categoryID()));
                });
    }

    /**
     * Reads orders.
     *
     * @return the order list
     */
    public static List<Order> getOrders() {

        final List<Order> orderList;
        try (Driver driver = createConnectionPool()) {
            driver.verifyConnectivity();
            orderList = driver.executableQuery(GET_ORDERS_QUERY)
                    .execute().records().stream()
                    .map(record -> record.get("ordersTree").as(Order.class))
                    .toList();
        } catch (Exception e) {
            logger.error("getOrders(): exception[{}]", e.getMessage());
            throw new RuntimeException(e);
        }
        orderList.forEach(Neo4jClient::printOrder);
        System.out.println("- ".repeat(50));
        return orderList;
    }

    /**
     * Logs order.
     *
     * @param order the order
     */
    private static void printOrder(Order order) {

        System.out.printf("order shipName[%s]%n", order.shipName());
        System.out.printf("\t customer companyName[%s]%n", order.customer().companyName());
        System.out.printf("\t shipper companyName[%s]%n", order.shipper().companyName());
        System.out.printf("\t employee fullName[%s %s], title[%s]%n",
                order.employee().firstName(), order.employee().lastName(), order.employee().title());
        Optional.of(order.employee().managers()).filter(Predicate.not(List::isEmpty)).map(List::getFirst)
                .ifPresent(employee -> System.out.printf("\t\t manager fullName[%s %s], title[%s]%n",
                        employee.firstName(), employee.lastName(), employee.title()));
        order.employee().territories().forEach(territory -> {
            System.out.printf("\t\t territory territoryDescription[%s]%n", territory.territoryDescription());
            territory.regions().forEach(region ->
                    System.out.printf("\t\t\t region regionDescription[%s]%n", region.regionDescription()));
        });
        final Consumer<OrderItem> orderItemConsumer = orderItem -> {
            System.out.printf("\t\t quantity[%s]%n", orderItem.quantity());
            final Product product = orderItem.product();
            System.out.printf("\t\t product productName[%s]%n", product.productName());
            final String companyName = Optional.ofNullable(product.supplier())
                    .map(Supplier::companyName).orElse("N/A");
            System.out.printf("\t\t\t supplier companyName[%s]%n", companyName);
            final String categoryName = Optional.ofNullable(product.partOfCategories())
                    .map(List::getFirst).map(Category::categoryName).orElse("N/A");
            System.out.println("\t\t\t partOfCategories");
            System.out.printf("\t\t\t\t category categoryName[%s]%n", categoryName);
        };
        order.orderedProducts().forEach(orderItem -> {
            System.out.println("\t ordered products");
            orderItemConsumer.accept(orderItem);
        });
        order.containedProducts().forEach(orderItem -> {
            System.out.println("\t contained products");
            orderItemConsumer.accept(orderItem);
        });
    }

    /**
     * Creates the connection pool.
     *
     * @return the Neo4j graph database driver
     */
    private static Driver createConnectionPool() {

        final String host = Tools.getEnvStrOrDefault("NEO4J_HOST", "localhost");
        final int port = Tools.getEnvIntOrDefault("NEO4J_PORT", 7687);
        final String user = Tools.getEnvStrOrDefault("NEO4J_USER", "neo4j");
        final String password = Tools.getEnvStrOrDefault("NEO4J_PASSWORD", "mikimiki");
        final String uri = String.format("bolt://%s:%d", host, port);
        return GraphDatabase.driver(uri, AuthTokens.basic(user, password));
    }
}
