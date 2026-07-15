package kp;

import kp.clients.cassandra.CassandraClient;
import kp.clients.chroma.ChromaClient;
import kp.clients.elasticsearch.ElasticsearchDatabaseClient;
import kp.clients.mongodb.MongoDbClient;
import kp.clients.mysql.MySqlClient;
import kp.clients.neo4j.Neo4jClient;
import kp.clients.oracle.OracleClient;
import kp.clients.postgresql.PostgreSqlClient;
import kp.clients.redis.RedisDatabaseClient;
import kp.clients.sql_server.SqlServerClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

/**
 * An application that accesses various databases.
 */
public class Application {
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    // There is IDEA prompt to remove 'public' but
    // Java 21+ deprecated non-public main methods; will be removed in future versions

    /**
     * The primary entry point for launching the application.
     *
     * @param args the arguments
     */
    public static void main(String[] args) {
        final String arg = args != null && args.length > 0 ? args[0] : "";
        switch (arg.substring(0, 3)) {
            case "CAS" -> CassandraClient.process(arg);
            case "CHR" -> ChromaClient.process(arg);
            case "ELA" -> ElasticsearchDatabaseClient.process(arg);
            case "MON" -> MongoDbClient.process(arg);
            case "MYS" -> MySqlClient.process(arg);
            case "NEO" -> Neo4jClient.process(arg);
            case "ORA" -> OracleClient.process(arg);
            case "POS" -> PostgreSqlClient.process(arg);
            case "RED" -> RedisDatabaseClient.process(arg);
            case "SQL" -> SqlServerClient.process(arg);
            default -> logger.warn("main(): default case, arg[{}]", arg);
        }
    }
}
