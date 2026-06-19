package kp.clients.mongodb;

import com.mongodb.client.*;
import kp.domain.company.Department;
import kp.domain.company.Employee;
import kp.domain.company.Title;
import kp.utils.Tools;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static kp.clients.mongodb.MongoDbConstants.DEPARTMENTS_AND_EMPLOYEES_PIPELINE;

/**
 * MongoDB client.
 */
public class MongoDbClient {
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    /**
     * Processes the queries.
     *
     * @param key the key
     */
    public static void process(String key) {

        try (MongoClient mongoClient = createMongoClient()) {

            final MongoDatabase database = mongoClient.getDatabase(
                    Tools.getEnvStrOrDefault("MONGODB_DATABASE", "kp_database"));
            switch (key) {
                case "MON_01":
                    discoverCollectionsAndIndexes(database);
                    break;
                case "MON_02":
                    getDepartmentsAndEmployees(database);
                    break;
                default:
                    logger.warn("process(): unhandled key[{}]", key);
                    break;
            }
        } catch (Exception e) {
            logger.error("process(): Exception[{}]", e.getMessage());
            throw new RuntimeException(e);
        }
        logger.info("process(): key[{}]", key);
    }

    /**
     * List the existing collections and review the indexes applied to them.
     *
     * @param database the MongoDatabase
     */
    private static void discoverCollectionsAndIndexes(MongoDatabase database) {

        logger.info("### Discovering Collections and Indexes ###");
        database.listCollectionNames().forEach(collectionName -> {
            logger.info("Collection name[{}]", collectionName);
            database.getCollection(collectionName).listIndexes().forEach(index ->
                    logger.info("\t Index name[{}], keys{}", index.getString("name"),
                            Optional.of(index.get("key"))
                                    .filter(Document.class::isInstance).map(Document.class::cast)
                                    .map(Document::toJson).orElse(""))
            );
        });
    }

    /**
     * Queries departments and their employees using the aggregation framework,
     * loading the result into domain records.
     *
     * @param database the MongoDatabase
     */
    private static void getDepartmentsAndEmployees(MongoDatabase database) {

        final MongoCollection<Document> departmentCollection = database.getCollection("departments");
        final AggregateIterable<Document> result = departmentCollection.aggregate(DEPARTMENTS_AND_EMPLOYEES_PIPELINE);
        final Function<Document, Employee> mapper = employeeDocument -> {
            final int employeeId = employeeDocument.getInteger("id");
            final String firstName = employeeDocument.getString("firstName");
            final String lastName = employeeDocument.getString("lastName");
            final Title title = Title.fromString(
                    Optional.ofNullable(employeeDocument.getString("title")).orElse(""));
            return new Employee(employeeId, firstName, lastName, title);
        };
        final List<Department> departments = new ArrayList<>();

        result.forEach(document -> {
            final int departmentId = document.getInteger("id");
            final String name = document.getString("name");

            final List<Document> employeeDocumentList =
                    Optional.ofNullable(document.getList("employees", Document.class)).orElse(List.of());
            final List<Employee> employeeList = employeeDocumentList.stream().map(mapper).toList();
            departments.add(new Department(departmentId, name, employeeList));
        });
        Tools.printDepartments(departments);
    }

    /**
     * Creates the connection to MongoDB.
     * The MongoClient natively handles connection pooling.
     *
     * @return the MongoClient.
     */
    private static MongoClient createMongoClient() {

        final String host = Tools.getEnvStrOrDefault("MONGODB_HOST", "localhost");
        final int port = Tools.getEnvIntOrDefault("MONGODB_PORT", 27017);
        final String user = Tools.getEnvStrOrDefault("MONGODB_USER", "admin");
        final String password = Tools.getEnvStrOrDefault("MONGODB_PASSWORD", "mikimiki");
        final String uri = String.format("mongodb://%s:%s@%s:%d/?authSource=%s",
                user, password, host, port, user);
        return MongoClients.create(uri);
    }
}