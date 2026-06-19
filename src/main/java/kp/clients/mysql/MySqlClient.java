package kp.clients.mysql;

import com.zaxxer.hikari.HikariDataSource;
import kp.clients.RelationalDatabaseClient;
import kp.clients.RelationalDatabaseConstants;
import kp.utils.Tools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.sql.Connection;
import java.sql.SQLException;

import static kp.clients.mysql.MySqlConstants.SCHEMA_DISCOVERY_QUERIES;

/**
 * MySQL client.
 */
public class MySqlClient {
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    /**
     * Processes the queries.
     *
     * @param key the key
     */
    public static void process(String key) {

        try (HikariDataSource dataSource = createConnectionPool();
             Connection connection = dataSource.getConnection()) {
            switch (key) {
                case "MYS_01":
                    SCHEMA_DISCOVERY_QUERIES.forEach((label, query) ->
                            RelationalDatabaseClient.executeQuery(connection, query, label));
                    break;
                case "MYS_02":
                    RelationalDatabaseConstants.DEPARTMENTS_AND_EMPLOYEES_QUERIES.forEach((label, query) ->
                            RelationalDatabaseClient.executeQuery(connection, query, label));
                    RelationalDatabaseClient.getDepartmentsAndEmployees(connection);
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
     * Creates the connection pool.
     *
     * @return the Hikari datasource.
     */
    private static HikariDataSource createConnectionPool() {

        final String host = Tools.getEnvStrOrDefault("MY_SQL_HOST", "localhost");
        final int port = Tools.getEnvIntOrDefault("MY_SQL_PORT", 3306);
        final String database = Tools.getEnvStrOrDefault("MY_SQL_DATABASE", "kp_database");
        final String user = Tools.getEnvStrOrDefault("MY_SQL_USER", "kp");
        final String password = Tools.getEnvStrOrDefault("MY_SQL_PASSWORD", "mikimiki");
        final String jdbcUrl = String.format("jdbc:mysql://%s:%d/%s", host, port, database);
        return RelationalDatabaseClient.createHikariDataSource(
                "com.mysql.cj.jdbc.Driver", jdbcUrl, user, password);
    }
}