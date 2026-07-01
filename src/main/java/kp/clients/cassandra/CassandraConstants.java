package kp.clients.cassandra;

import com.datastax.oss.driver.api.core.cql.Row;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;

/**
 * Constants for Cassandra database.
 * <p>
 * Using the keyspace name 'study28'.
 * </p>
 */
public class CassandraConstants {

    static final String SELECT_DEPARTMENTS_CQL = "SELECT id, name FROM study28.departments";
    static final String SELECT_EMPLOYEES_CQL =
            "SELECT department_id, id, first_name, last_name, title FROM study28.employees";
    static final BiFunction<Row, String, String> ROW_STR_FUN = (row, name) -> Optional.ofNullable(row.getString(name)).orElse("");

    /**
     * Schema discovery queries using Cassandra's system_schema keyspace.
     * <p>
     * The {@code system_schema} keyspace is the Cassandra equivalent of SQL's
     * {@code information_schema} — it exposes metadata about keyspaces, tables,
     * columns, indexes, types, and more.
     * </p>
     */
    static final Map<String, String> SCHEMA_DISCOVERY_QUERIES = new LinkedHashMap<>();

    static {
        SCHEMA_DISCOVERY_QUERIES.put("Discover tables and their columns", """
                SELECT table_name, column_name, type, kind
                FROM system_schema.columns
                WHERE keyspace_name = 'study28'
                ALLOW FILTERING
                """);
        SCHEMA_DISCOVERY_QUERIES.put("Discover tables", """
                SELECT table_name, comment, bloom_filter_fp_chance, compaction
                FROM system_schema.tables
                WHERE keyspace_name = 'study28'
                """);
        SCHEMA_DISCOVERY_QUERIES.put("Discover indexes", """
                SELECT table_name, index_name, kind, options
                FROM system_schema.indexes
                WHERE keyspace_name = 'study28'
                ALLOW FILTERING
                """);
    }

    /**
     * Private constructor to prevent instantiation.
     */
    private CassandraConstants() {
        throw new IllegalStateException("Utility class");
    }
}