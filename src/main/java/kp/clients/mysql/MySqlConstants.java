package kp.clients.mysql;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Constants for MySQL database.
 */
public class MySqlConstants {

    static final String DEPARTMENTS_AND_EMPLOYEES_QUERY = """
            SELECT
              d.id AS department_id,
              d.name,
              e.id AS employee_id,
              e.first_name,
              e.last_name,
              e.title
            FROM departments d
            LEFT JOIN employees e ON d.id = e.department_id
            """;

    static final Map<String, String> SCHEMA_DISCOVERY_QUERIES = new LinkedHashMap<>();
    static final Map<String, String> DEPARTMENTS_AND_EMPLOYEES_QUERIES = new LinkedHashMap<>();

    static {
        SCHEMA_DISCOVERY_QUERIES.put("Discover all tables and their columns", """
                SELECT
                    table_name,
                    column_name,
                    data_type,
                    is_nullable
                FROM information_schema.columns
                WHERE table_schema = 'kp_database'
                ORDER BY table_name, ordinal_position
                """);

        SCHEMA_DISCOVERY_QUERIES.put("Discover foreign key relationships", """
                SELECT
                    table_name AS source_table,
                    column_name AS source_column,
                    referenced_table_name AS target_table,
                    referenced_column_name AS target_column
                FROM information_schema.key_column_usage
                WHERE referenced_table_name IS NOT NULL
                    AND table_schema = 'kp_database'
                ORDER BY source_table, target_table
                """);

        SCHEMA_DISCOVERY_QUERIES.put("Discover Indexes", """
                SELECT
                    table_name,
                    index_name,
                    column_name
                FROM information_schema.statistics
                WHERE table_schema = 'kp_database'
                ORDER BY table_name, index_name, seq_in_index
                """);

        DEPARTMENTS_AND_EMPLOYEES_QUERIES.put("Get departments and employees",
                DEPARTMENTS_AND_EMPLOYEES_QUERY);
    }

    /**
     * Private constructor to prevent instantiation.
     */
    private MySqlConstants() {
        throw new IllegalStateException("Utility class");
    }
}