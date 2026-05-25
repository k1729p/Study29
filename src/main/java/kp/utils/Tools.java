package kp.utils;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

/**
 *
 */
public class Tools {
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
