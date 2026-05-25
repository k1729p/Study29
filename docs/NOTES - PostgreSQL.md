Query 1: Table & Column Structural Blueprint
Instead of just asking for table names, this pulls the column names and data types. In a relational database, a table name by itself isn't fully descriptive; knowing that departments has an id of type integer or uuid is the true equivalent of finding a node's properties.

Filters used: table_schema = 'public'. This is important because it filters out internal PostgreSQL system tables.

Query 2: Table Relationships (Foreign Keys)
This is the direct relational equivalent to Neo4j's MATCH (source)-[relation]->(target). It navigates the information schema to show you exactly which tables point to which other tables, forming the "edges" of your relational schema.

Query 3: Indexes
Your instinct was correct: indexes are vital to include. While graph databases inherently use pointer-chasing for relationships, relational databases heavily rely on indexes (like B-Trees) to make searches and table joins performant. This query scans the pg_indexes catalog and prints the exact SQL definition used to build each index (e.g., CREATE INDEX idx_dept_name ON departments USING btree (name)).