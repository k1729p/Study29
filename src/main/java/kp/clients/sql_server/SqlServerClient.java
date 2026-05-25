package kp.clients.sql_server;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import kp.domain.company.Department;
import kp.domain.company.Employee;
import kp.domain.company.Title;
import kp.utils.Tools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.sql.*;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.IntStream;

import static kp.clients.sql_server.SqlServerConstants.*;

/**
 * Microsoft SQL Server client.
 */
public class SqlServerClient {
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    /**
     * Processes the queries based on the operational key.
     *
     * @param key the action execution key
     */
    public static void process(String key) {

        try (HikariDataSource dataSource = createConnectionPool();
             Connection connection = dataSource.getConnection()) {
            switch (key) {
                case "SQL_01":
                    SCHEMA_DISCOVERY_QUERIES.forEach((label, query) ->
                            executeQuery(connection, query, rs -> printResult(rs, label)));
                    break;
                case "SQL_02":
                    DEPARTMENTS_AND_EMPLOYEES_QUERIES.forEach((label, query) ->
                            executeQuery(connection, query, rs -> printResult(rs, label)));
                    getDepartmentsAndEmployees(connection);
                    break;
                default:
                    logger.warn("process(): unhandled key[{}]", key);
                    break;
            }
        } catch (SQLException e) {
            logger.error("process(): SQLException[{}]", e.getMessage());
            throw new RuntimeException(e);
        }
        logger.info("process(): key[{}]", key);
    }

    /**
     * Executes query.
     */
    private static void executeQuery(Connection connection, String query, Consumer<ResultSet> consumer) {

        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            consumer.accept(resultSet);
        } catch (SQLException e) {
            logger.error("executeQuery(): SQLException[{}]", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * Prints the query result in a tabular format.
     */
    private static void printResult(ResultSet resultSet, String label) {

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
     * Loads raw results from standard ResultSet.
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
     * Queries departments and their employees, parsing data to specialized domain records.
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
        printDepartments(departments);
    }

    /**
     * Prints structural hierarchy details of Departments.
     */
    private static void printDepartments(List<Department> departments) {

        logger.info("- ".repeat(20));
        departments.forEach(department -> {
            logger.info("department id[{}]", department.id());
            logger.info("department name[{}]", department.name());
            department.employees().forEach(employee -> {
                logger.info("\t employee id[{}]", employee.id());
                logger.info("\t employee name[{} {}]", employee.firstName(), employee.lastName());
                logger.info("\t employee title[{}]", employee.title());
            });
            logger.info("- ".repeat(20));
        });
    }

    /**
     * Configures the active connection pool.
     */
    private static HikariDataSource createConnectionPool() {

        final String host = Tools.getEnvOrDefault("SQL_SERVER_HOST", "localhost");
        final String port = Tools.getEnvOrDefault("SQL_SERVER_PORT", "1433");
        final String database = Tools.getEnvOrDefault("SQL_SERVER_DATABASE", "master");
        final String user = Tools.getEnvOrDefault("SQL_SERVER_USER", "sa");
        final String password = Tools.getEnvOrDefault("SQL_SERVER_PASSWORD", "01+AZ+az");

        // Appended security encryption options necessary for modern local dockerized instances
        final String jdbcUrl = String.format("jdbc:sqlserver://%s:%s;databaseName=%s;encrypt=true;trustServerCertificate=true;",
                host, port, database);

        return new HikariDataSource(getHikariConfig(jdbcUrl, user, password));
    }

    /**
     * Loads full configurations parameters into Hikari target.
     */
    private static HikariConfig getHikariConfig(String jdbcUrl, String user, String password) {

        final HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(user);
        config.setPassword(password);

        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setConnectionTimeout(30_000);
        config.setIdleTimeout(600_000);
        config.setMaxLifetime(1_800_000);
        config.setInitializationFailTimeout(1);

        config.setDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        return config;
    }
}