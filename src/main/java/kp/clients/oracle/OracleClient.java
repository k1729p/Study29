package kp.clients.oracle;

import com.zaxxer.hikari.HikariDataSource;
import kp.clients.RelationalDatabaseClient;
import kp.clients.RelationalDatabaseConstants;
import kp.utils.Tools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.sql.Connection;
import java.sql.SQLException;

import static kp.clients.oracle.OracleConstants.SCHEMA_DISCOVERY_QUERIES;

/**
 * Oracle database client.
 */
public class OracleClient {
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
                case "ORA_01" -> SCHEMA_DISCOVERY_QUERIES.forEach((label, query) ->
                        RelationalDatabaseClient.executeQuery(connection, query, label));
                case "ORA_02" -> {
                    RelationalDatabaseConstants.DEPARTMENTS_AND_EMPLOYEES_QUERIES.forEach((label, query) ->
                            RelationalDatabaseClient.executeQuery(connection, query, label));
                    RelationalDatabaseClient.getDepartmentsAndEmployees(connection);
                }
                default -> logger.warn("process(): unhandled key[{}]", key);
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

        final String host = Tools.getEnvStrOrDefault("ORACLE_HOST", "localhost");
        final int port = Tools.getEnvIntOrDefault("ORACLE_PORT", 1521);
        final String database = Tools.getEnvStrOrDefault("ORACLE_DATABASE", "FREEPDB1");
        final String user = Tools.getEnvStrOrDefault("ORACLE_USER", "system");
        final String password = Tools.getEnvStrOrDefault("ORACLE_PASSWORD", "mikimiki");
        final String jdbcUrl = String.format("jdbc:oracle:thin:@//%s:%d/%s", host, port, database);
        return RelationalDatabaseClient.createHikariDataSource(
                "oracle.jdbc.OracleDriver", jdbcUrl, user, password);
    }
}