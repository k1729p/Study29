package kp.clients.sql_server;

import com.zaxxer.hikari.HikariDataSource;
import kp.clients.RelationalDatabaseClient;
import kp.clients.RelationalDatabaseConstants;
import kp.utils.Tools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.sql.Connection;
import java.sql.SQLException;

import static kp.clients.sql_server.SqlServerConstants.SCHEMA_DISCOVERY_QUERIES;

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
                            RelationalDatabaseClient.executeQuery(connection, query, label));
                    break;
                case "SQL_02":
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
        return new HikariDataSource(RelationalDatabaseClient.buildHikariConfig(
                "com.microsoft.sqlserver.jdbc.SQLServerDriver", jdbcUrl, user, password));
    }
}