package kp.utils;

import kp.domain.company.Department;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.List;
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
     */
    public static String getEnvOrDefault(String key, String defaultValue) {
        return Optional.ofNullable(System.getenv(key))
                .filter(Predicate.not(String::isBlank)).orElse(defaultValue);
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
