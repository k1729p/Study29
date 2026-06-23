# Neo4j
### Using Northwind Dataset with Neo4j
1. Northwind initialization deletes all data from Neo4j database. \
   Nodes 'Department' and 'Employee' are deleted! \
   Node 'Employee' is replaced by node 'Employee' from Northwind dataset. 
2. Before running menu point "Neo4j Northwind Initialization" \
   execute batch file "datasets\Northwind\copy_to_Docker.bat". \
   It copies CSV files in Docker container 'neo4j'.

### Documentation
- [Cypher Cheat Sheet](https://neo4j.com/docs/cypher-cheat-sheet/25/all/)
- [Cypher Manual](https://neo4j.com/docs/cypher-manual/)
- [Java Manual](https://neo4j.com/docs/java-manual/)

---
- Nodes have labels.
- Relationships have types.
- When you create a unique constraint, an index is automatically created for that property.
---
1. The API-Ready queries: \
    To package this for an API, you should use Map Projection and List Comprehension. \
    This avoids returning multiple rows and instead gives you \
    one clean JSON object containing all the related data.
2. Query features:
    - Map Projection (node { .prop }): \
       This explicitly selects only the properties you want, which keeps the payload small and \
       prevents leaking sensitive internal node data.
    - Pattern Comprehension ([ (pattern) | result ]): \
       This is essentially a subquery that looks for matching paths and collects them into a list. \
       It is extremely efficient in Neo4j.
    - Index [0]: \
       Since an Order usually has only one Customer, Shipper, or Employee, adding [0] at the end \
       of the comprehension converts the list into a single object (or null if not found).
    - Nested Logic: \
       Supplier is nested inside the items list and territories is nested inside the employee object. \
       This mirrors the natural hierarchy of a JSON response.

---
Graph Cypher query - based on 'GET_ORDERS_QUERY' \
This creates a "star" graph around the Order node in one go.
<pre>
MATCH
path1 = (order:Order),
path2 = (order)-[:ORDERS]->(orderedProduct:Product)-[:PART_OF]->(:Category),
path3 = (order)<-[:PURCHASED]-(customer:Customer),
path4 = (order)<-[:SHIPS]-(shipper:Shipper),
path5 = (order)-[:CONTAINS]->(containedProduct:Product)<-[:SUPPLIES]-(supplier:Supplier),
path6 = (order)-[:ORDERS]->(product_3:Product)<-[:SUPPLIES]-(supplier:Supplier),
path7 = (order)<-[:SOLD]-(employee:Employee)-[:IN_TERRITORY]->
(territory:Territory)-[:IN_REGION]->(region:Region),
path8 = (employee)-[:REPORTS_TO]->(manager:Employee)
LIMIT 1
RETURN path1, path2, path3, path4, path5, path6, path7, path8
</pre>

---
# AI Asked Explains: Graph Intelligence vs. Relational/NoSQL

* The Paradigm Shift: You manage data via tables (Oracle, Postgres), documents (MongoDB), or inverted indexes (ElasticSearch). Neo4j manages data as Nodes (entities/records) and Relationships (edges/foreign keys) treated as first-class citizens.
* The "Intelligence" Part: It goes beyond simple CRUD operations. It runs native graph algorithms (e.g., PageRank, Pathfinding, Community Detection) directly inside the engine to uncover hidden patterns and fraud networks. [1, 2, 3, 4, 5]

## Technical Mapping to Your Stack

| Concept [6, 7, 8, 9, 10] | Your Current Stack (SQL/NoSQL) | Neo4j Equivalent |
|--|--|--|
| Data Rows | Rows in Oracle/Postgres or Docs in Mongo | Nodes with key-value properties. |
| Join Tables | Many-to-Many junction tables or FK columns | Relationships (explicit, directed, named paths). |
| Query Language | SQL, MQL, or Elastic DSL | Cypher (a declarative visual graph language). |
| Indexes | B-Trees (Postgres) or Lucene (Elastic) | Index-free adjacency (pointers to next nodes). |

## Why This Matters to a Senior Dev

* No More JOIN Agony: In Oracle or Postgres, a 5-level deep recursive JOIN kills performance. In Neo4j, traversing relationships takes $O(1)$ constant time per hop, because pointers physically link the data on disk.
* Beyond Kafka & Elastic Search: While Kafka streams events and Elastic searches text, Neo4j maps how those events and entities relate structurally over time.
* SpringBoot & Quarkus Integration: Neo4j integrates natively with spring-boot-starter-data-neo4j. It supports reactive repositories, OGM (Object-Graph Mapping), and Quarkus extension ecosystems.
* GenAI / LLM Synergy: Neo4j acts as a GraphRAG (Retrieval-Augmented Generation) backend. It feeds precise vector and contextual relationship data into LLMs, outperforming standard vector-only databases. [11, 12, 13, 14, 15]
