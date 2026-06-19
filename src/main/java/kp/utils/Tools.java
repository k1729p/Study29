package kp.utils;

import kp.domain.company.Department;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

/**
 * The tools.
 */
public class Tools {
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    /**
     * Prints departments to standard logging.
     *
     * @param departments the departments
     */
    public static void printDepartments(List<Department> departments) {

        final StringBuilder strBld = new StringBuilder();
        strBld.append("- ".repeat(20));
        departments.forEach(department -> {
            strBld.append(System.lineSeparator()).append("department").append(System.lineSeparator());
            strBld.append(String.format("   id[%d]%n", department.id()));
            strBld.append(String.format("   name[%s]%n", department.name()));
            if(department.employees().isEmpty()) {
                strBld.append("- ".repeat(20));
                return;
            }
            strBld.append("   employees").append(System.lineSeparator());
            department.employees().forEach(employee -> {
                strBld.append("      employee").append(System.lineSeparator());
                strBld.append(String.format("         id[%d]%n", employee.id()));
                strBld.append(String.format("         name[%s %s]%n",
                        employee.firstName(), employee.lastName()));
                strBld.append(String.format("         title[%s]%n", employee.title()));
            });
            strBld.append("- ".repeat(20));
        });
        logger.info(strBld.toString());
    }


    /**
     * Helper method to fetch environment variables or return a default value.
     *
     * @param key the key
     * @param defaultValue the default value
     * @return the environment variable
     */
    public static int getEnvIntOrDefault(String key, int defaultValue) {
        return Optional.ofNullable(System.getenv(key))
                .filter(Predicate.not(String::isBlank))
                .map(Integer::parseInt)
                .orElse(defaultValue);
    }

    /**
     * Helper method to fetch environment variables or return a default value.
     *
     * @param key the key
     * @param defaultValue the default value
     * @return the environment variable
     */
    public static String getEnvStrOrDefault(String key, String defaultValue) {
        return Optional.ofNullable(System.getenv(key))
                .filter(Predicate.not(String::isBlank)).orElse(defaultValue);
    }

    /**
     * Extracts string from map.
     *
     * @param map the data map
     * @param key the key
     * @return the string
     */
    public static String extractString(Map<String, Object> map, String key) {
        return switch (map.get(key)) {
            case String str -> str;
            case null -> "";
            default -> map.get(key).toString();
        };
    }

    /**
     * Extracts number from map.
     *
     * @param map the data map
     * @param key the key
     * @return the number
     */
    public static int extractNumber(Map<String, Object> map, String key) {
        return switch (map.get(key)) {
            case Integer i -> i;
            case String str -> {
                try {
                    yield Integer.parseInt(str);
                } catch (NumberFormatException e) {
                    logger.warn("extractNumber(): Cannot parse [{}] as integer", str);
                    yield 0;
                }
            }
            case null, default -> 0;
        };
    }

    /**
     * Performs a sleep using this timeout value.
     *
     * @param timeout the timeout
     */
    public static void sleep(int timeout) {
        try {
            TimeUnit.SECONDS.sleep(timeout);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
