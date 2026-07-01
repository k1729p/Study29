package kp;

import kp.clients.cassandra.CassandraClient;
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
        switch (arg) {
            case "CAS_01":
            case "CAS_02":
                CassandraClient.process(arg);
                break;
            case "ELA_01":
            case "ELA_02":
                ElasticsearchDatabaseClient.process(arg);
                break;
            case "MON_01":
            case "MON_02":
                MongoDbClient.process(arg);
                break;
            case "MYS_01":
            case "MYS_02":
                MySqlClient.process(arg);
                break;
            case "NEO_01":
            case "NEO_02":
            case "NEO_03":
            case "NEO_04":
            case "NEO_05":
                Neo4jClient.process(arg);
                break;
            case "ORA_01":
            case "ORA_02":
                OracleClient.process(arg);
                break;
            case "POS_01":
            case "POS_02":
                PostgreSqlClient.process(arg);
                break;
            case "RED_01":
            case "RED_02":
            case "RED_03":
            case "RED_04":
                RedisDatabaseClient.process(arg);
                break;
            case "SQL_01":
            case "SQL_02":
                SqlServerClient.process(arg);
                break;
            default:
                logger.warn("main(): default case");
                break;
        }
    }
}
