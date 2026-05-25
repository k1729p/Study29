package kp.clients.oracle;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Constants for Oracle database.
 */
public class OracleConstants {
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
        SCHEMA_DISCOVERY_QUERIES.put("Discover tables and their columns", """
                SELECT
                    table_name,
                    column_name,
                    data_type,
                    nullable
                FROM user_tab_columns
                WHERE table_name IN (
                    'DEPARTMENTS',
                    'EMPLOYEES'
                )
                ORDER BY table_name, column_id
                """);

        SCHEMA_DISCOVERY_QUERIES.put("Discover foreign key relationships", """
                SELECT
                    a.table_name AS source_table,
                    a.column_name AS source_column,
                    c_pk.table_name AS target_table,
                    c_pk.column_name AS target_column
                FROM user_cons_columns a
                JOIN user_constraints c ON a.constraint_name = c.constraint_name
                JOIN user_cons_columns c_pk ON c.r_constraint_name = c_pk.constraint_name
                WHERE
                    c.constraint_type = 'R' AND
                    a.table_name = 'EMPLOYEES' AND
                    c_pk.table_name = 'DEPARTMENTS'
                ORDER BY a.table_name, a.position
                """);

        SCHEMA_DISCOVERY_QUERIES.put("Discover indexes", """
                SELECT
                    i.table_name,
                    i.index_name,
                    c.column_name
                FROM user_indexes i
                JOIN user_ind_columns c ON i.index_name = c.index_name
                WHERE i.table_name IN (
                    'DEPARTMENTS',
                    'EMPLOYEES'
                )
                ORDER BY i.table_name, i.index_name, c.column_position
                """);

        DEPARTMENTS_AND_EMPLOYEES_QUERIES.put("Get departments and employees",
                DEPARTMENTS_AND_EMPLOYEES_QUERY);
    }

    /**
     * Private constructor to prevent instantiation.
     */
    private OracleConstants() {
        throw new IllegalStateException("Utility class");
    }
}