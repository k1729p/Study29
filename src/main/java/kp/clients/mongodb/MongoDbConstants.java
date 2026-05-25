package kp.clients.mongodb;

import org.bson.Document;

import java.util.List;

/**
 * Constants for MongoDB database.
 */
public class MongoDbConstants {
    /**
     * Aggregation pipeline equivalent to the SQL LEFT JOIN.
     */
    static final List<Document> DEPARTMENTS_AND_EMPLOYEES_PIPELINE = List.of(
            new Document("$sort", new Document("id", 1)),
            new Document("$lookup", new Document("from", "employees")
                    .append("localField", "id")
                    .append("foreignField", "departmentId")
                    .append("as", "employees")),
            new Document("$addFields", new Document("employees",
                    new Document("$sortArray", new Document("input", "$employees")
                            .append("sortBy", new Document("id", 1)))))
    );

    /**
     * Private constructor to prevent instantiation.
     */
    private MongoDbConstants() {
        throw new IllegalStateException("Utility class");
    }
}