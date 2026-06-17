package kp.clients;

import com.zaxxer.hikari.HikariConfig;
import kp.domain.company.Department;
import kp.domain.company.Employee;
import kp.domain.company.Title;
import kp.utils.Tools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.sql.*;
import java.util.*;
import java.util.stream.IntStream;

import static kp.clients.RelationalDatabaseConstants.DEPARTMENTS_AND_EMPLOYEES_QUERY;

/**
 * Relational database client.
 */
public class RelationalDatabaseClient {
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    /**
     * Executes query.
     *
     * @param connection the connection
     * @param query      the query
     * @param label      the label
     */
    public static void executeQuery(Connection connection, String query, String label) {

        try (PreparedStatement statement = connection.prepareStatement(query);
             final ResultSet resultSet = statement.executeQuery()) {
            printQueryResults(resultSet, label);
        } catch (SQLException e) {
            logger.error("executeQuery(): SQLException[{}]", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * Prints the query results.
     *
     * @param resultSet the result set
     * @param label     the label
     */
    private static void printQueryResults(ResultSet resultSet, String label) {

        final Map<String, List<String>> dataMap = new LinkedHashMap<>();
        final Map<String, Integer> lengthMap = new LinkedHashMap<>();
        loadResult(resultSet, dataMap, lengthMap);

        final StringBuilder strBld = new StringBuilder();
        strBld.append(String.format("### %s ###%n", label));
        final Iterator<List<String>> iterator = dataMap.values().iterator();
        if (!iterator.hasNext()) {
            logger.info(strBld.append("No results are found").toString());
            return;
        }
        final int rowCount = iterator.next().size();
        int lineLength = 1;
        for (Integer value : lengthMap.values()) {
            lineLength = lineLength + value + 3;
        }
        strBld.append(String.format("%s%n", "-".repeat(lineLength))).append("| ");
        dataMap.keySet().forEach(header -> {
            final String format = String.format("%%-%ds | ", lengthMap.get(header));
            strBld.append(String.format(format, header));
        });
        strBld.append(String.format("%n%s%n", "-".repeat(lineLength)));
        IntStream.range(0, rowCount).forEach(i -> {
            strBld.append("| ");
            dataMap.forEach((key, value) -> {
                final String format = String.format("%%-%ds | ", lengthMap.get(key));
                strBld.append(String.format(format, value.get(i)));
            });
            strBld.append("\n");
        });
        strBld.append(String.format("%s", "-".repeat(lineLength)));
        logger.info(strBld.toString());
    }

    /**
     * Load results dynamically into maps for formatting.
     *
     * @param resultSet the result set
     * @param dataMap   the data map
     * @param lengthMap the length map
     */
    private static void loadResult(ResultSet resultSet,
                                   Map<String, List<String>> dataMap, Map<String, Integer> lengthMap) {
        try {
            final ResultSetMetaData metaData = resultSet.getMetaData();
            final int columnCount = metaData.getColumnCount();
            while (resultSet.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    String key = metaData.getColumnLabel(i);
                    lengthMap.put(key, lengthMap.getOrDefault(key, key.length()));
                    final List<String> list = dataMap.getOrDefault(key, new ArrayList<>());
                    final String result = Optional.ofNullable(resultSet.getString(i)).orElse("");
                    list.add(result);
                    lengthMap.put(key, Math.max(result.length(), lengthMap.get(key)));
                    dataMap.put(key, list);
                }
            }
        } catch (SQLException e) {
            logger.error("loadResult(): SQLException[{}]", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates the Hikari configuration.
     *
     * @param driverClass the driver class
     * @param jdbcUrl     the JDBC URL
     * @param user        the user
     * @param password    the password
     * @return the configuration
     */
    public static HikariConfig buildHikariConfig(String driverClass, String jdbcUrl,
                                                 String user, String password) {

        final HikariConfig config = new HikariConfig();
        config.setDriverClassName(driverClass);
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(user);
        config.setPassword(password);
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setConnectionTimeout(30_000);
        config.setIdleTimeout(600_000);
        config.setMaxLifetime(1_800_000);
        config.setInitializationFailTimeout(1);
        return config;
    }

    /**
     * Queries departments and their employees, loading the result into domain records.
     *
     * @param connection the connection
     */
    public static void getDepartmentsAndEmployees(Connection connection) {

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
            logger.error("getDepartmentsAndEmployees(): SQLException[{}]", e.getMessage());
            throw new RuntimeException(e);
        }
        final List<Department> departments = departmentNames.entrySet().stream()
                .map(entry -> {
                    final int id = entry.getKey();
                    final String name = entry.getValue();
                    return new Department(id, name, departmentEmployees.get(id));
                })
                .toList();
        Tools.printDepartments(departments);
    }
}
