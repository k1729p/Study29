package kp.clients.redis;

import kp.domain.company.Department;
import kp.domain.company.Employee;
import kp.domain.company.Title;
import kp.utils.Tools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.ConnectionPoolConfig;
import redis.clients.jedis.RedisClient;
import redis.clients.jedis.params.ScanParams;
import redis.clients.jedis.resps.ScanResult;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

import java.lang.invoke.MethodHandles;
import java.time.Duration;
import java.util.*;
import java.util.function.Predicate;

/**
 * Redis database client.
 */
public class RedisDatabaseClient {
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final RedisClient CLIENT = createClient();
    private static final JsonMapper JSON_MAPPER = new JsonMapper();
    private static final TypeReference<Map<String, Object>> MAP_TYPE_REFERENCE = new TypeReference<>() {
    };

    /**
     * Processes the queries.
     *
     * @param key the execution argument key
     */
    public static void process(String key) {
        switch (key) {
            case "RED_01":
                discoverSchema();
                break;
            case "RED_02":
                getDepartmentsAndEmployees();
                break;
            case "RED_03":
                logger.info("- ".repeat(20));
                ProbabilisticDataTypes.checkItemMembership(CLIENT);
                ProbabilisticDataTypes.calculateSetCardinality(CLIENT);
                ProbabilisticDataTypes.countItemFrequency(CLIENT);
                ProbabilisticDataTypes.calculateQuantiles(CLIENT);
                ProbabilisticDataTypes.calculateRankings(CLIENT);
                break;
            default:
                logger.warn("process(): unhandled key[{}]", key);
                break;
        }
        logger.info("process(): key[{}]", key);
    }

    /**
     * Inspects key spaces and system diagnostic metrics using non-blocking SCAN.
     */
    private static void discoverSchema() {

        final StringBuilder strBld = new StringBuilder();
        strBld.append("### Keyspace Information ###");
        strBld.append(String.format("%n%s%n", "-".repeat(50)));
        strBld.append(CLIENT.info("keyspace").trim());
        strBld.append(String.format("%n%s%n%n", "-".repeat(50)));

        strBld.append("### Sample of Extant Key Schemas ###");
        final Map<String, String> dataMap = new LinkedHashMap<>();
        scanKeysWithPattern(null).forEach(key -> dataMap.put(key, CLIENT.type(key)));
        int[] lengths = {"Key".length(), "Type".length()};
        dataMap.forEach((key, value) -> {
            lengths[0] = Math.max(key.length(), lengths[0]);
            lengths[1] = Math.max(value.length(), lengths[1]);
        });
        int lineLength = lengths[0] + lengths[1] + 7;
        strBld.append(String.format("%n%s%n", "-".repeat(lineLength))).append("| ");
        final String pattern1 = "%-" + lengths[0] + "s | ";
        strBld.append(String.format(pattern1, "Key"));
        final String pattern2 = "%-" + lengths[1] + "s | ";
        strBld.append(String.format(pattern2, "Type"));
        strBld.append(String.format("%n%s%n", "-".repeat(lineLength)));
        dataMap.forEach((key, value) -> {
            strBld.append("| ");
            strBld.append(String.format(pattern1, key));
            strBld.append(String.format(pattern2, value));
            strBld.append("\n");
        });
        strBld.append(String.format("%s", "-".repeat(lineLength)));
        logger.info(strBld.toString());
    }

    /**
     * Gets departments and employees.
     */
    private static void getDepartmentsAndEmployees() {

        final Map<Integer, String> departmentIdAndNameMap = getDepartmentIdAndNameMap();
        final Map<Integer, List<Employee>> departmentEmployeesMap = getDepartmentEmployeesMap();
        final List<Department> departments = departmentIdAndNameMap.keySet().stream().sorted()
                .map(departmentId -> new Department(departmentId,
                        departmentIdAndNameMap.get(departmentId),
                        departmentEmployeesMap.getOrDefault(departmentId, new ArrayList<>())))
                .toList();
        Tools.printDepartments(departments);
    }

    /**
     * Gets department id and name map.
     *
     * @return the map with the department id and name
     */
    private static Map<Integer, String> getDepartmentIdAndNameMap() {

        final List<String> departmentKeys = scanKeysWithPattern("department:*", "department:\\d+");
        final Map<Integer, String> departmentIdAndNameMap = new HashMap<>();
        departmentKeys.forEach(departmentKey -> {
            final Map<String, Object> departmentData = Optional.ofNullable(CLIENT.get(departmentKey))
                    .filter(Predicate.not(String::isEmpty))
                    .map(depJson -> JSON_MAPPER.readValue(depJson, MAP_TYPE_REFERENCE))
                    .orElse(Map.of());
            final int id = Tools.extractNumber(departmentData, "id");
            final String name = id != 0 ? Tools.extractString(departmentData, "name") : "";
            departmentIdAndNameMap.put(id, name);
        });
        return departmentIdAndNameMap;
    }

    /**
     * Gets department employees map.
     *
     * @return the map with the department employees
     */
    private static Map<Integer, List<Employee>> getDepartmentEmployeesMap() {

        final List<String> employeeKeys = scanKeysWithPattern("employee:*", "employee:\\d+");
        final Map<Integer, List<Employee>> departmentEmployeesMap = new HashMap<>();
        employeeKeys.forEach(employeeKey -> {
            final Optional<String> empJsonOpt = Optional.ofNullable(CLIENT.get(employeeKey))
                    .filter(Predicate.not(String::isEmpty));
            if (empJsonOpt.isEmpty()) {
                return;
            }
            final Map<String, Object> employeeData = JSON_MAPPER.readValue(empJsonOpt.get(), MAP_TYPE_REFERENCE);
            final int id = Tools.extractNumber(employeeData, "id");
            final int departmentId = Tools.extractNumber(employeeData, "departmentId");
            if (id == 0 || departmentId == 0) {
                return;
            }
            final Employee employee = new Employee(id,
                    Tools.extractString(employeeData, "firstName"),
                    Tools.extractString(employeeData, "lastName"),
                    Title.fromString(Tools.extractString(employeeData, "title")));
            departmentEmployeesMap.computeIfAbsent(departmentId, _ -> new ArrayList<>()).add(employee);
        });
        return departmentEmployeesMap;
    }

    /**
     * Redis pool initialization.
     *
     * @return the Redis client
     */
    private static RedisClient createClient() {

        final String host = Tools.getEnvStrOrDefault("REDIS_HOST", "localhost");
        final int port = Tools.getEnvIntOrDefault("REDIS_PORT", 6379);
        final ConnectionPoolConfig poolConfig = new ConnectionPoolConfig();
        poolConfig.setMaxTotal(10);
        poolConfig.setMaxIdle(5);
        poolConfig.setMinIdle(2);
        poolConfig.setMaxWait(Duration.ofSeconds(30));
        return RedisClient.builder().hostAndPort(host, port).poolConfig(poolConfig).build();
    }

    /**
     * Scans Redis keys using SCAN command (non-blocking cursor-based iteration).
     * Supports optional pattern matching. Use null for "*" pattern.
     *
     * @param pattern       the glob pattern for key matching (null means "*")
     * @param filterPattern the filter pattern
     * @return list of keys matching the pattern
     */
    private static List<String> scanKeysWithPattern(String pattern, String filterPattern) {

        return scanKeysWithPattern(pattern).stream()
                .filter(key -> key.matches(filterPattern)).sorted().toList();
    }

    /**
     * Scans Redis keys using SCAN command (non-blocking cursor-based iteration).
     * Supports optional pattern matching. Use null for "*" pattern.
     *
     * @param pattern the glob pattern for key matching (null means "*")
     * @return list of keys matching the pattern
     */
    private static List<String> scanKeysWithPattern(String pattern) {

        final String scanPattern = pattern != null ? pattern : "*";
        final List<String> keyList = new ArrayList<>();
        String cursor = "0";
        do {
            final ScanParams scanParams = new ScanParams().match(scanPattern);
            final ScanResult<String> scanResult = CLIENT.scan(cursor, scanParams);
            keyList.addAll(scanResult.getResult());
            cursor = scanResult.getCursor();
        } while (!cursor.equals("0"));
        return keyList;
    }

}