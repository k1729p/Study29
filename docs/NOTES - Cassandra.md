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
| Definition | Meaning                                        |
|------------|------------------------------------------------|
| CAP        | Consistency, Availability, Partition Tolerance |
| CAS        | Compare-And-Set                                |
| CQL        | Cassandra Query Language                       |
| LSM        | Log Structured Merge                           |
| LWW        | Last-Write-Wins                                |

### Notes
The Apache Cassandra Java Driver uses its own binary protocol (CQL native protocol v4/v5). \
The Apache Cassandra Java Driver 4.x manages its own internal connection pool per host via CqlSession \
Using DataStax Java driver for Cassandra.

### Draft with Design Ideas - Logical Data Model for 'Department':
https://cassandra.apache.org/doc/latest/cassandra/developing/data-modeling/data-modeling_logical.html#hotel-logical-data-model

CREATE TYPE study28.address (
street_name text,
house_number text,
postal_code text,
locality text,
province text,
country text,
)

CREATE TABLE ${KEYSPACE}.departments (
id int PRIMARY KEY,
name text,
start_date date,
end_date date,
notes text,
keywords list<text>,
image text,
employees set<text>
) WITH comment = 'Find information about a department'

CREATE TABLE ${KEYSPACE}.employees (
department_id int,
id int,
first_name text,
last_name text,
title text,
phone text,
mail text,
address frozen<address>,
PRIMARY KEY (department_id, id)
) WITH comment = 'Find information about an employee'

with 'K' is labeled partition key column
with 'C' is labeled clustering key column

departments
    K id
      name

employees
    K id
      first_name
      last_name
      title

allocations_by_department   (or maybe 'employees_by_department')
    K employee_id
    C department_id
    first_name
    last_name
    title

allocations_by_title
    K employee_id
    C title
    first_name
    last_name
    department_id

---