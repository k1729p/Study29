package kp.clients.chroma;

import com.fasterxml.jackson.databind.JsonNode;
import kp.domain.company.Department;
import kp.domain.company.Employee;
import kp.domain.company.Title;
import kp.utils.Tools;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.function.Predicate;

import static kp.clients.chroma.ChromaConstants.*;

/**
 * Chroma database client executing raw REST API requests.
 */
public class ChromaClient {
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    /**
     * Processes the database queries.
     *
     * @param key the execution argument key
     */
    public static void process(String key) {
        switch (key) {
            case "CHR_01" -> discoverSchema();
            case "CHR_02" -> getDepartmentsAndEmployees();
            default -> logger.warn("process(): unhandled key[{}]", key);
        }
        logger.info("process(): key[{}]", key);
    }

    /**
     * Discovers the schema.
     */
    private static void discoverSchema() {

        final StringBuilder strBld = new StringBuilder();
        strBld.append("### Discovering Collections ###");
        final List<Map<String, Object>> collections = fetchCollections();
        collections.forEach(collection -> {
            strBld.append(System.lineSeparator()).append("-".repeat(60)).append(System.lineSeparator());
            strBld.append("Collection").append(System.lineSeparator());
            final String id = String.valueOf(collection.get("id"));
            strBld.append(String.format("  id[%s]%n", id));
            strBld.append(String.format("  name[%s]%n", collection.get("name")));
            final int count = Optional.ofNullable(fetchRecordsFromCollection(id))
                    .map(node -> node.get("ids"))
                    .filter(JsonNode::isArray).map(JsonNode::size).orElse(0);
            strBld.append(String.format("  records count[%d]%n", count));
        });
        strBld.append(System.lineSeparator()).append("-".repeat(60));
        logger.info(strBld.toString());
    }

    /**
     * Retrieves departments and employees and prints them.
     */
    private static void getDepartmentsAndEmployees() {

        final List<Map<String, Object>> collections = fetchCollections();
        if (collections.isEmpty()) {
            logger.error("getDepartmentsAndEmployees(): empty collections");
            return;
        }
        final String departmentCollectionId = findCollectionIdByName(collections, "departments");
        if (departmentCollectionId == null) {
            logger.error("getDepartmentsAndEmployees(): department collection id is null");
            return;
        }
        final String employeeCollectionId = findCollectionIdByName(collections, "employees");
        if (employeeCollectionId == null) {
            logger.error("getDepartmentsAndEmployees(): employee collection id is null");
            return;
        }
        final List<Department> departmentList = getDepartmentList(departmentCollectionId, employeeCollectionId);
        logger.info("### Get departments and employees ###");
        Tools.printDepartments(departmentList);
    }

    /**
     * Finds collection id by name
     *
     * @param collections the collections map
     * @param targetName  the target name
     * @return the collection
     */
    private static String findCollectionIdByName(List<Map<String, Object>> collections, String targetName) {

        return collections.stream()
                .filter(c -> targetName.equals(c.get("name")))
                .map(c -> String.valueOf(c.get("id")))
                .findFirst()
                .orElse(null);
    }

    /**
     * Gets department map
     *
     * @param departmentCollectionId department collection id
     * @param employeeCollectionId   employee collection id
     * @return the department map
     */
    private static List<Department> getDepartmentList(String departmentCollectionId, String employeeCollectionId) {

        final JsonNode departmentRecords = fetchRecordsFromCollection(departmentCollectionId);
        final Optional<JsonNode> idsNodeOpt = JSON_NODE_FUN.apply(departmentRecords, "ids");
        final Optional<JsonNode> documentsNodeOpt = JSON_NODE_FUN.apply(departmentRecords, "documents");
        if (idsNodeOpt.isEmpty() || documentsNodeOpt.isEmpty()) {
            return List.of();
        }
        final Map<Integer, Department> departmentMap = new LinkedHashMap<>();
        for (int i = 0; i < idsNodeOpt.get().size(); i++) {
            final int id = NODE_TO_INT_FUN.apply(idsNodeOpt, i);
            final Department department = new Department(id,
                    NODE_TO_STR_FUN.apply(documentsNodeOpt, i), new ArrayList<>());
            departmentMap.put(id, department);
        }
        addEmployees(employeeCollectionId, departmentMap);
        return new ArrayList<>(departmentMap.values());
    }

    /**
     * Adds employees to the department map
     *
     * @param employeeCollectionId employee collection id
     * @param departmentMap        the department map
     */
    private static void addEmployees(String employeeCollectionId, Map<Integer, Department> departmentMap) {

        final JsonNode employeeRecords = fetchRecordsFromCollection(employeeCollectionId);
        final Optional<JsonNode> idsNodeOpt = JSON_NODE_FUN.apply(employeeRecords, "ids");
        final Optional<JsonNode> metadatasNodeOpt = JSON_NODE_FUN.apply(employeeRecords, "metadatas");
        if (idsNodeOpt.isEmpty() || metadatasNodeOpt.isEmpty()) {
            return;
        }
        for (int i = 0; i < idsNodeOpt.get().size(); i++) {
            final int j = i;
            final Optional<JsonNode> metaOpt = metadatasNodeOpt.map(arg -> arg.get(j))
                    .filter(Predicate.not(JsonNode::isNull));
            if (metaOpt.isEmpty()) {
                continue;
            }
            final Department department = departmentMap.get(NODE_P_TO_INT_FUN.apply(metaOpt, "departmentId"));
            if (department == null) {
                continue;
            }
            final Employee employee = new Employee(NODE_TO_INT_FUN.apply(idsNodeOpt, i),
                    NODE_P_TO_STR_FUN.apply(metaOpt, "firstName"),
                    NODE_P_TO_STR_FUN.apply(metaOpt, "lastName"),
                    Title.fromString(NODE_P_TO_STR_FUN.apply(metaOpt, "title")));
            department.employees().add(employee);
        }
    }

    /**
     * Fetches the collections.
     *
     * @return the collections
     */
    private static List<Map<String, Object>> fetchCollections() {

        final List<Map<String, Object>> collections = new ArrayList<>();
        try {
            final HttpResponse<String> response = HTTP_CLIENT.send(
                    COLLECTIONS_REQUEST, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != HttpStatus.SC_OK) {
                logger.error("fetchCollections(): returned HTTP status[{}]", response.statusCode());
                return collections;
            }
            collections.addAll(JSON_MAPPER.readValue(response.body(), COL_TYPE_REFERENCE));
        } catch (IOException | InterruptedException e) {
            logger.error("fetchCollections(): exception[{}]", e.getMessage());
        }
        return collections;
    }

    /**
     * Fetches records from collection.
     *
     * @param collectionId the collection id
     * @return the JSON node
     */
    private static JsonNode fetchRecordsFromCollection(String collectionId) {

        try {
            final HttpResponse<String> response = HTTP_CLIENT.send(
                    RECORDS_REQUEST_FUN.apply(collectionId), HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != HttpStatus.SC_OK) {
                logger.error("fetchRecordsFromCollection(): returned HTTP status[{}]", response.statusCode());
                return null;
            }
            return JSON_MAPPER.readTree(response.body());
        } catch (IOException | InterruptedException e) {
            logger.error("fetchRecordsFromCollection(): exception[{}]", e.getMessage());
            return null;
        }
    }

}