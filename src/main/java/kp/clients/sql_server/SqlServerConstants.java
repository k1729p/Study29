package kp.clients.sql_server;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Constants for Microsoft SQL Server database.
 */
public class SqlServerConstants {
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
                FROM INFORMATION_SCHEMA.COLUMNS
                WHERE
                    table_schema = 'dbo' AND
                    table_name  IN (
                        'departments',
                        'employees'
                    )
                ORDER BY TABLE_NAME, ORDINAL_POSITION
                """);

        SCHEMA_DISCOVERY_QUERIES.put("Discover foreign key relationships", """
                SELECT
                    OBJECT_NAME(fk.parent_object_id) AS source_table,
                    cp.name AS source_column,
                    OBJECT_NAME(fk.referenced_object_id) AS target_table,
                    cr.name AS target_column
                FROM sys.foreign_keys fk
                INNER JOIN sys.foreign_key_columns fkc ON fk.object_id = fkc.constraint_object_id
                INNER JOIN sys.columns cp ON fkc.parent_object_id = cp.object_id AND fkc.parent_column_id = cp.column_id
                INNER JOIN sys.columns cr ON fkc.referenced_object_id = cr.object_id AND fkc.referenced_column_id = cr.column_id
                ORDER BY source_table, target_table
                """);

        SCHEMA_DISCOVERY_QUERIES.put("Discover Indexes", """
                SELECT
                    t.name AS table_name,
                    i.name AS index_name,
                    i.type_desc AS index_definition
                FROM sys.indexes i
                INNER JOIN sys.tables t ON i.object_id = t.object_id
                WHERE i.is_hypothetical = 0 AND i.index_id > 0
                ORDER BY t.name, i.name
                """);

        DEPARTMENTS_AND_EMPLOYEES_QUERIES.put("Get departments and employees",
                DEPARTMENTS_AND_EMPLOYEES_QUERY);
    }

    /**
     * Private constructor to prevent instantiation.
     */
    private SqlServerConstants() {
        throw new IllegalStateException("Utility class");
    }
}