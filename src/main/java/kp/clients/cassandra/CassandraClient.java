package kp.clients.cassandra;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import kp.domain.company.Department;
import kp.domain.company.Employee;
import kp.domain.company.Title;
import kp.utils.Tools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.stream.StreamSupport;

import static kp.clients.cassandra.CassandraConstants.*;

/**
 * Cassandra client.
 * <p>
 * Using the DataStax Java driver.<br>
 * The Apache Cassandra Java Driver ({@link CqlSession}) manages
 * its own internal per-host connection pool.
 * </p>
 */
public class CassandraClient {
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    /**
     * Processes the queries.
     *
     * @param key the key
     */
    public static void process(String key) {

        try (CqlSession session = createSession()) {
            switch (key) {
                case "CAS_01" -> SCHEMA_DISCOVERY_QUERIES.forEach((label, cql) -> {
                    final ResultSet resultSet = session.execute(cql);
                    printQueryResults(resultSet, label);
                });
                case "CAS_02" -> getDepartmentsAndEmployees(session);
                default -> logger.warn("process(): unhandled key[{}]", key);
            }
        }
        logger.info("process(): key[{}]", key);
    }

    /**
     * Creates and returns a connected {@link CqlSession}.
     *
     * @return the connected CQL session
     */
    private static CqlSession createSession() {

        final String host = Tools.getEnvStrOrDefault("CASSANDRA_HOST", "localhost");
        final int port = Tools.getEnvIntOrDefault("CASSANDRA_PORT", 9042);
        final String datacenter = Tools.getEnvStrOrDefault("CASSANDRA_DC", "kp-datacenter");
        return CqlSession.builder()
                .addContactPoint(new InetSocketAddress(host, port))
                .withLocalDatacenter(datacenter)
                .build();
    }

    /**
     * Prints the CQL result set as a formatted ASCII table,
     * using the same style as {@code RelationalDatabaseClient}.
     *
     * @param resultSet the result set
     * @param label     the label shown above the table
     */
    private static void printQueryResults(ResultSet resultSet, String label) {

        final List<String> columnNames = Optional.of(resultSet.getColumnDefinitions())
                .map(columnDefinitions -> StreamSupport
                        .stream(columnDefinitions.spliterator(), false)
                        .map(def -> def.getName().asInternal())
                        .toList())
                .orElse(List.of());
        if (columnNames.isEmpty()) {
            logger.info("### {} ###\n No columns returned", label);
            return;
        }
        final Map<String, List<String>> dataMap = new LinkedHashMap<>();
        final Map<String, Integer> lengthMap = new LinkedHashMap<>();
        columnNames.forEach(name -> {
            dataMap.put(name, new ArrayList<>());
            lengthMap.put(name, name.length());
        });
        StreamSupport.stream(resultSet.spliterator(), false).forEach(row ->
                columnNames.forEach(name -> {
                    final String value = Optional.ofNullable(row.getObject(name)).map(Object::toString).orElse("");
                    dataMap.get(name).add(value);
                    lengthMap.merge(name, value.length(), Math::max);
                })
        );
        final int rowCount = dataMap.get(columnNames.getFirst()).size();
        if (rowCount == 0) {
            logger.info("### {} ###\n No results are found", label);
            return;
        }
        int lineLength = 1;
        for (int w : lengthMap.values()) {
            lineLength += w + 3;
        }
        final String separator = "-".repeat(lineLength);
        final StringBuilder strBld = new StringBuilder();
        strBld.append(String.format("### %s ###%n", label));
        strBld.append(separator).append(System.lineSeparator()).append("| ");
        columnNames.forEach(name -> strBld.append(
                String.format("%-" + lengthMap.get(name) + "s | ", name)));
        strBld.append(System.lineSeparator()).append(separator).append(System.lineSeparator());

        for (int i = 0; i < rowCount; i++) {
            strBld.append("| ");
            final int index = i;
            columnNames.forEach(name -> strBld.append(
                    String.format("%-" + lengthMap.get(name) + "s | ", dataMap.get(name).get(index))));
            strBld.append(System.lineSeparator());
        }
        strBld.append(separator);
        logger.info(strBld.toString());
    }

    /**
     * Queries departments and employees separately, assembles them into
     * domain records, and delegates printing to {@link Tools#printDepartments}.
     *
     * @param session the CQL session
     */
    private static void getDepartmentsAndEmployees(CqlSession session) {

        final ResultSet departmentResultSet = session.execute(SELECT_DEPARTMENTS_CQL);

        final Map<Integer, String> departmentNames = new LinkedHashMap<>();
        final Map<Integer, List<Employee>> departmentEmployees = new LinkedHashMap<>();
        StreamSupport.stream(departmentResultSet.spliterator(), false).forEach(row -> {
            final int departmentId = row.getInt("id");
            departmentNames.putIfAbsent(departmentId, ROW_STR_FUN.apply(row, "name"));
            departmentEmployees.putIfAbsent(departmentId, new ArrayList<>());
        });

        final ResultSet employeeResultSet = session.execute(SELECT_EMPLOYEES_CQL);

        StreamSupport.stream(employeeResultSet.spliterator(), false).forEach(row -> {
            final int depId = row.getInt("department_id");
            if (!departmentEmployees.containsKey(depId)) {
                logger.warn("getDepartmentsAndEmployees(): employee references unknown department_id[{}]", depId);
                return;
            }
            departmentEmployees.get(depId).add(new Employee(
                    row.getInt("id"),
                    ROW_STR_FUN.apply(row, "first_name"),
                    ROW_STR_FUN.apply(row, "last_name"),
                    Title.fromString(row.getString("title"))));
        });
        final List<Department> departmentList = departmentNames.entrySet().stream()
                .map(entry -> {
                    final int id = entry.getKey();
                    final String name = entry.getValue();
                    return new Department(id, name, departmentEmployees.get(id));
                }).toList();
        Tools.printDepartments(departmentList);
    }
}