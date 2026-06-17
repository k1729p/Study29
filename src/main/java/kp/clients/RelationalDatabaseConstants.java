package kp.clients;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Constants for relational database.
 */
public class RelationalDatabaseConstants {
    public static final String DEPARTMENTS_AND_EMPLOYEES_QUERY = """
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
    public static final Map<String, String> DEPARTMENTS_AND_EMPLOYEES_QUERIES = new LinkedHashMap<>();
    static {
        DEPARTMENTS_AND_EMPLOYEES_QUERIES.put("Get departments and employees",
                DEPARTMENTS_AND_EMPLOYEES_QUERY);
    }
}
