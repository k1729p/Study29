# Apache Cassandra
### Cassandra Documentation
- [Features](https://cassandra.apache.org/doc/latest/cassandra/architecture/overview.html#features)
- [Data Modeling](https://cassandra.apache.org/doc/latest/cassandra/developing/data-modeling/intro.html)
- [Glossary](https://cassandra.apache.org/_/glossary.html)

### Schema discovery queries

- `system_schema.columns` — all columns with their CQL type and `kind` (partition key, clustering, regular, static)
- `system_schema.tables` — table-level metadata (compaction strategy, bloom filter, comments)
- `system_schema.indexes` — secondary indexes

Cassandra's `system_schema` keyspace is the structural equivalent of SQL's `information_schema`.

`ALLOW FILTERING` is required for the columns and indexes queries because `keyspace_name` is not the partition key in those tables. In production workloads this would be a concern, but for a local schema-discovery tool it is the correct and standard approach.

### Acronyms
| Definition | Meaning                  |
|--|--------------------------|
| CQL | Cassandra Query Language |
| LWW | Last-Write-Wins          |

### Notes
The Apache Cassandra Java Driver uses its own binary protocol (CQL native protocol v4/v5). \
The Apache Cassandra Java Driver 4.x manages its own internal connection pool per host via CqlSession \
Using DataStax Java driver for Cassandra.

---