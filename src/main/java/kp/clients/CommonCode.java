package kp.clients;

import kp.domain.company.Department;
import kp.domain.company.Employee;
import kp.domain.company.Title;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

class CommonCode {
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
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    /**
     * Queries departments and their employees, loading the result into domain records.
     *
     * @param connection the connection
     */
    static void getDepartmentsAndEmployees(Connection connection) {

        final Map<Integer, String> departmentNames = new LinkedHashMap<>();
        final Map<Integer, List<Employee>> departmentEmployees = new LinkedHashMap<>();
        try (PreparedStatement statement = connection.prepareStatement(DEPARTMENTS_AND_EMPLOYEES_QUERY);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                final int depId = resultSet.getInt("department_id");
                final String name = resultSet.getString("name");
                departmentNames.putIfAbsent(depId, name);
                departmentEmployees.putIfAbsent(depId, new ArrayList<>());
                final int empId = resultSet.getInt("employee_id");
                if (resultSet.wasNull()) {
                    continue;
                }
                final String firstName = resultSet.getString("first_name");
                final String lastName = resultSet.getString("last_name");
                final String titleStr = resultSet.getString("title");
                final Title title = Title.fromString(titleStr);
                departmentEmployees.get(depId).add(new Employee(empId, firstName, lastName, title));
            }
        } catch (SQLException e) {
            logger.error("queryAndLoadToRecords(): SQLException[{}]", e.getMessage());
            throw new RuntimeException(e);
        }
        final List<Department> departments = departmentNames.entrySet().stream()
                .map(entry -> {
                    final int id = entry.getKey();
                    final String name = entry.getValue();
                    return new Department(id, name, departmentEmployees.get(id));
                })
                .toList();
        printDepartments(departments);
    }

    /**
     * Prints departments.
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
                logger.info("\t employee name[{} {}]",
                        employee.firstName(), employee.lastName());
                logger.info("\t employee title[{}]", employee.title());
            });
            logger.info("- ".repeat(20));
        });
    }
    // TODO Hikari
    // TODO CHECK IF RedisDatabaseClient::printDepartments IS NOT REDUNDANT
    // TODO CHECK IF MongoDbClient::printDepartments IS NOT REDUNDANT

}
