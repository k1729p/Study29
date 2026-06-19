package kp.clients.postgresql;

import com.zaxxer.hikari.HikariDataSource;
import kp.clients.RelationalDatabaseClient;
import kp.clients.RelationalDatabaseConstants;
import kp.utils.Tools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.sql.Connection;
import java.sql.SQLException;

import static kp.clients.postgresql.PostgreSqlConstants.SCHEMA_DISCOVERY_QUERIES;

/**
 * PostgreSQL client.
 */
public class PostgreSqlClient {
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
                case "POS_01":
                    SCHEMA_DISCOVERY_QUERIES.forEach((label, query) ->
                            RelationalDatabaseClient.executeQuery(connection, query, label));
                    break;
                case "POS_02":
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

        final String host = Tools.getEnvStrOrDefault("POSTGRESQL_HOST", "localhost");
        final int port = Tools.getEnvIntOrDefault("POSTGRESQL_PORT", 5432);
        final String database = Tools.getEnvStrOrDefault("POSTGRESQL_DATABASE", "postgres");
        final String user = Tools.getEnvStrOrDefault("POSTGRESQL_USER", "postgres");
        final String password = Tools.getEnvStrOrDefault("POSTGRESQL_PASSWORD", "mikimiki");
        final String jdbcUrl = String.format("jdbc:postgresql://%s:%d/%s", host, port, database);
        return RelationalDatabaseClient.createHikariDataSource(
                "org.postgresql.Driver", jdbcUrl, user, password);
    }

}