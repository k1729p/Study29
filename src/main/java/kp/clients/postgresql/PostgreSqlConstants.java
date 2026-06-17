package kp.clients.postgresql;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Constants for PostgreSql database.
 */
public class PostgreSqlConstants {
    static final Map<String, String> SCHEMA_DISCOVERY_QUERIES = new LinkedHashMap<>();

    static {
        SCHEMA_DISCOVERY_QUERIES.put("Discover all tables and their columns", """
                SELECT
                    table_name,
                    column_name,
                    data_type,
                    is_nullable
                FROM information_schema.columns
                WHERE table_schema = 'public'
                ORDER BY table_name, ordinal_position
                """);
        SCHEMA_DISCOVERY_QUERIES.put("Discover foreign key relationships", """
                SELECT
                    tc.table_name AS source_table,
                    kcu.column_name AS source_column,
                    ccu.table_name AS target_table,
                    ccu.column_name AS target_column
                FROM information_schema.table_constraints AS tc
                JOIN information_schema.key_column_usage AS kcu
                    ON tc.constraint_name = kcu.constraint_name
                    AND tc.table_schema = kcu.table_schema
                JOIN information_schema.constraint_column_usage AS ccu
                    ON ccu.constraint_name = tc.constraint_name
                WHERE tc.constraint_type = 'FOREIGN KEY'
                    AND tc.table_schema = 'public'
                ORDER BY source_table, target_table
                """);
        SCHEMA_DISCOVERY_QUERIES.put("Discover Indexes", """
                SELECT
                    tablename AS table_name,
                    indexname AS index_name,
                    indexdef AS index_definition
                FROM pg_indexes
                WHERE schemaname = 'public'
                ORDER BY tablename, indexname
                """);
    }

    /**
     * Private constructor to prevent instantiation.
     */
    private PostgreSqlConstants() {
        throw new IllegalStateException("Utility class");
    }
}