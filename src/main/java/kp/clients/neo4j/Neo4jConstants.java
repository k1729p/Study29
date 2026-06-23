package kp.clients.neo4j;

import java.util.List;
import java.util.Map;

/**
 * Constants for Neo4j database.
 */
public class Neo4jConstants {
    static final String JSON_RESPONSE_KEY = "response";

    static final List<String> SCHEMA_DISCOVERY_QUERIES = List.of(
            """
                    MATCH (source)-[relation]->(target)
                    ORDER BY labels(source), type(relation), labels(target)
                    RETURN DISTINCT labels(source), type(relation), labels(target)""",
            "MATCH (n) ORDER BY labels(n) RETURN DISTINCT labels(n)",
            "MATCH ()-[relation]->() ORDER BY type(relation) RETURN DISTINCT type(relation)");

    static final String DEPARTMENTS_AND_EMPLOYEES_QUERY = """
            MATCH (department:Department)
            OPTIONAL MATCH (employee:Employee)-[:WORKS_IN]->(department)
            ORDER BY department.id ASC, employee.id ASC
            RETURN department, collect(employee) AS employees
            """;

    static final List<String> NORTHWIND_INITIALIZATION_QUERIES_1 = List.of(
            // ############################################################ CREATING CONSTRAINTS
            """
                    CREATE CONSTRAINT category_categoryID IF NOT EXISTS
                    FOR (c:Category) REQUIRE (c.categoryID) IS UNIQUE""",
            """
                    CREATE CONSTRAINT customer_customerID IF NOT EXISTS
                    FOR (c:Customer) REQUIRE (c.customerID) IS UNIQUE""",
            """
                    CREATE CONSTRAINT employee_employeeID IF NOT EXISTS
                    FOR (e:Employee) REQUIRE (e.employeeID) IS UNIQUE""",
            """
                    CREATE CONSTRAINT order_orderID IF NOT EXISTS
                    FOR (o:Order) REQUIRE (o.orderID) IS UNIQUE""",
            """
                    CREATE CONSTRAINT product_productID IF NOT EXISTS
                    FOR (p:Product) REQUIRE (p.productID) IS UNIQUE""",
            """
                    CREATE CONSTRAINT region_regionID IF NOT EXISTS
                    FOR (r:Region) REQUIRE (r.regionID) IS UNIQUE""",
            """
                    CREATE CONSTRAINT shipper_shipperID IF NOT EXISTS
                    FOR (s:Shipper) REQUIRE (s.shipperID) IS UNIQUE""",
            """
                    CREATE CONSTRAINT supplier_supplierID IF NOT EXISTS
                    FOR (s:Supplier) REQUIRE (s.supplierID) IS UNIQUE""",
            """
                    CREATE CONSTRAINT territory_territoryID IF NOT EXISTS
                    FOR (t:Territory) REQUIRE (t.territoryID) IS UNIQUE"""
    );

    static final List<String> NORTHWIND_INITIALIZATION_QUERIES_2 = List.of(
            // ############################################################ PRUNING
            "MATCH (n) DETACH DELETE n",
            // ############################################################ CREATING NODES
            """
                    LOAD CSV WITH HEADERS FROM 'file:///Category.csv' AS row
                    WITH row WHERE
                        row.categoryID IS NOT NULL AND
                        row.categoryID <> "null" AND
                        row.categoryID <> "NULL"
                    CREATE (n:Category)
                    SET n.categoryID = toInteger(trim(row.categoryID)),
                        n.categoryName = trim(row.categoryName),
                        n.description = trim(row.description)""",
            """
                    LOAD CSV WITH HEADERS FROM 'file:///Customer.csv' AS row
                    WITH row WHERE
                        row.customerID IS NOT NULL AND
                        row.customerID <> "null" AND
                        row.customerID <> "NULL"
                    CREATE (n:Customer)
                    SET n.customerID = trim(row.customerID),
                        n.companyName = trim(row.companyName),
                        n.contactName = trim(row.contactName),
                        n.contactTitle = trim(row.contactTitle),
                        n.phone = trim(row.phone),
                        n.fax = trim(row.fax),
                        n.country = trim(row.country),
                        n.region = trim(row.region),
                        n.city = trim(row.city),
                        n.postalCode = trim(row.postalCode),
                        n.address = trim(row.address)""",
            """
                    LOAD CSV WITH HEADERS FROM 'file:///Employee.csv' AS row
                    WITH row WHERE
                        row.employeeID IS NOT NULL AND
                        row.employeeID <> "null" AND
                        row.employeeID <> "NULL"
                    CREATE (n:Employee)
                    SET n.employeeID = toInteger(trim(row.employeeID)),
                        n.firstName = trim(row.firstName),
                        n.lastName = trim(row.lastName),
                        n.title = trim(row.title),
                        n.titleOfCourtesy = trim(row.titleOfCourtesy),
                        n.birthDate = trim(row.birthDate),
                        n.hireDate = trim(row.hireDate),
                        n.notes = trim(row.notes),
                        n.photoPath = trim(row.photoPath),
                        n.homePhone = trim(row.homePhone),
                        n.extension = trim(row.extension),
                        n.country = trim(row.country),
                        n.region = trim(row.region),
                        n.city = trim(row.city),
                        n.postalCode = trim(row.postalCode),
                        n.address = trim(row.address)""",
            """
                    LOAD CSV WITH HEADERS FROM 'file:///Order.csv' AS row
                    WITH row WHERE
                        row.orderID IS NOT NULL AND
                        row.orderID <> "null" AND
                        row.orderID <> "NULL"
                    CREATE (n:Order)
                    SET n.orderID = toInteger(trim(row.orderID)),
                        n.freight = trim(row.freight),
                        n.orderDate = trim(row.orderDate),
                        n.requiredDate = trim(row.requiredDate),
                        n.shippedDate = trim(row.shippedDate),
                        n.shipName = trim(row.shipName),
                        n.shipCountry = trim(row.shipCountry),
                        n.shipRegion = trim(row.shipRegion),
                        n.shipCity = trim(row.shipCity),
                        n.shipPostalCode = trim(row.shipPostalCode),
                        n.shipAddress = trim(row.shipAddress)""",
            """
                    LOAD CSV WITH HEADERS FROM 'file:///Product.csv' AS row
                    WITH row WHERE
                        row.productID IS NOT NULL AND
                        row.productID <> "null" AND
                        row.productID <> "NULL"
                    CREATE (n:Product)
                    SET n.productID = toInteger(trim(row.productID)),
                        n.productName = trim(row.productName),
                        n.unitPrice = trim(row.unitPrice),
                        n.unitsInStock = trim(row.unitsInStock),
                        n.unitsOnOrder = trim(row.unitsOnOrder),
                        n.reorderLevel = trim(row.reorderLevel),
                        n.discontinued = trim(row.discontinued)""",
            """
                    LOAD CSV WITH HEADERS FROM 'file:///Region.csv' AS row
                    WITH row WHERE
                        row.regionID IS NOT NULL AND
                        row.regionID <> "null" AND
                        row.regionID <> "NULL"
                    CREATE (n:Region)
                    SET n.regionID = toInteger(trim(row.regionID)),
                        n.regionDescription = trim(row.regionDescription)""",
            """
                    LOAD CSV WITH HEADERS FROM 'file:///Shipper.csv' AS row
                    WITH row WHERE
                        row.shipperID IS NOT NULL AND
                        row.shipperID <> "null" AND
                        row.shipperID <> "NULL"
                    CREATE (n:Shipper)
                    SET n.shipperID = toInteger(trim(row.shipperID)),
                        n.companyName = trim(row.companyName),
                        n.phone = trim(row.phone)""",
            """
                    LOAD CSV WITH HEADERS FROM 'file:///Supplier.csv' AS row
                    WITH row WHERE
                        row.supplierID IS NOT NULL AND
                        row.supplierID <> "null" AND
                        row.supplierID <> "NULL"
                    CREATE (n:Supplier)
                    SET n.supplierID = toInteger(trim(row.supplierID)),
                        n.companyName = trim(row.companyName),
                        n.contactName = trim(row.contactName),
                        n.contactTitle = trim(row.contactTitle),
                        n.phone = trim(row.phone),
                        n.fax = trim(row.fax),
                        n.homePage = trim(row.homePage),
                        n.country = trim(row.country),
                        n.city = trim(row.city),
                        n.region = trim(row.region),
                        n.postalCode = trim(row.postalCode),
                        n.address = trim(row.address)""",
            """
                    LOAD CSV WITH HEADERS FROM 'file:///Territory.csv' AS row
                    WITH row WHERE
                        row.territoryID IS NOT NULL AND
                        row.territoryID <> "null" AND
                        row.territoryID <> "NULL"
                    CREATE (n:Territory)
                    SET n.territoryID = toInteger(trim(row.territoryID)),
                        n.territoryDescription = trim(row.territoryDescription)""",
            // ############################################################ CREATING RELATIONS
            """
                    LOAD CSV WITH HEADERS FROM 'file:///Customer-PURCHASED-Order.csv' AS row
                    MATCH (customer:Customer { customerID: row.customerID })
                    MATCH (order:Order { orderID: toInteger(trim(row.orderID)) })
                    MERGE (customer)-[:PURCHASED]->(order)""",
            """
                    LOAD CSV WITH HEADERS FROM 'file:///Employee-IN_TERRITORY-Territory.csv' AS row
                    MATCH (employee:Employee { employeeID: toInteger(trim(row.employeeID)) })
                    MATCH (territory:Territory { territoryID: toInteger(trim(row.territoryID)) })
                    MERGE (employee)-[:IN_TERRITORY]->(territory)""",
            """
                    LOAD CSV WITH HEADERS FROM 'file:///Employee-REPORTS_TO-Employee.csv' AS row
                    MATCH (employee_1:Employee { employeeID: toInteger(trim(row.employeeID_1)) })
                    MATCH (employee_2:Employee { employeeID: toInteger(trim(row.employeeID_2)) })
                    MERGE (employee_1)-[:REPORTS_TO]->(employee_2)""",
            """
                    LOAD CSV WITH HEADERS FROM 'file:///Employee-SOLD-Order.csv' AS row
                    MATCH (employee:Employee { employeeID: toInteger(trim(row.employeeID)) })
                    MATCH (order:Order { orderID: toInteger(trim(row.orderID)) })
                    MERGE (employee)-[:SOLD]->(order)""",
            """
                    LOAD CSV WITH HEADERS FROM 'file:///Order-CONTAINS-Product.csv' AS row
                    MATCH (order:Order { orderID: toInteger(trim(row.orderID)) })
                    MATCH (product:Product { productID: toInteger(trim(row.productID)) })
                    MERGE (order)-[rel:CONTAINS]->(product)
                    ON CREATE SET
                        rel.unitPrice = trim(row.unitPrice),
                        rel.quantity = trim(row.quantity),
                        rel.discount = trim(row.discount)""",
            """
                    LOAD CSV WITH HEADERS FROM 'file:///Order-ORDERS-Product.csv' AS row
                    MATCH (order:Order { orderID: toInteger(trim(row.orderID)) })
                    MATCH (product:Product { productID: toInteger(trim(row.productID)) })
                    MERGE (order)-[rel:ORDERS]->(product)
                    ON CREATE SET
                        rel.unitPrice = trim(row.unitPrice),
                        rel.quantity = trim(row.quantity),
                        rel.discount = trim(row.discount)""",
            """
                    LOAD CSV WITH HEADERS FROM 'file:///Product-PART_OF-Category.csv' AS row
                    MATCH (product:Product { productID: toInteger(trim(row.productID)) })
                    MATCH (category:Category { categoryID: toInteger(trim(row.categoryID)) })
                    MERGE (product)-[:PART_OF]->(category)""",
            """
                    LOAD CSV WITH HEADERS FROM 'file:///Shipper-SHIPS-Order.csv' AS row
                    MATCH (shipper:Shipper { shipperID: toInteger(trim(row.shipperID)) })
                    MATCH (order:Order { orderID: toInteger(trim(row.orderID)) })
                    MERGE (shipper)-[:SHIPS]->(order)""",
            """
                    LOAD CSV WITH HEADERS FROM 'file:///Supplier-SUPPLIES-Product.csv' AS row
                    MATCH (supplier:Supplier { supplierID: toInteger(trim(row.supplierID)) })
                    MATCH (product:Product { productID: toInteger(trim(row.productID)) })
                    MERGE (supplier)-[:SUPPLIES]->(product)""",
            """
                    LOAD CSV WITH HEADERS FROM 'file:///Territory-IN_REGION-Region.csv' AS row
                    MATCH (territory:Territory { territoryID: toInteger(trim(row.territoryID)) })
                    MATCH (region:Region { regionID: toInteger(trim(row.regionID)) })
                    MERGE (territory)-[:IN_REGION]->(region)"""
    );

    static final List<String> NORTHWIND_READ_ALL_QUERIES = List.of(
            """
                    MATCH (category:Category)
                    MATCH (customer:Customer)
                    MATCH (employee:Employee)
                    MATCH (order:Order)
                    MATCH (product:Product)
                    MATCH (region:Region)
                    MATCH (shipper:Shipper)
                    MATCH (supplier:Supplier)
                    MATCH (territory:Territory)
                    RETURN
                        category.categoryID, category.categoryName,
                        customer.customerID, customer.companyName,
                        employee.employeeID, employee.lastName,
                        order.orderID, order.shipName,
                        product.productID, product.productName,
                        region.regionID, region.regionDescription,
                        shipper.shipperID, shipper.companyName,
                        supplier.supplierID, supplier.companyName,
                        territory.territoryID, territory.territoryDescription
                    LIMIT 1""",
            """
                    MATCH (c:Customer)-[:PURCHASED]->(o:Order)
                    RETURN c.customerID, c.companyName,
                           o.orderID, o.shipName
                    LIMIT 1""",
            """
                    MATCH (e:Employee)-[:IN_TERRITORY]->(t:Territory)
                    RETURN e.employeeID, e.lastName,
                           t.territoryID, t.territoryDescription
                    LIMIT 1""",
            """
                    MATCH (e1:Employee)-[:REPORTS_TO]->(e2:Employee)
                    RETURN e1.employeeID, e1.lastName,
                           e2.employeeID, e2.lastName
                    LIMIT 1""",
            """
                    MATCH (e:Employee)-[:SOLD]->(o:Order)
                    RETURN e.employeeID, e.lastName,
                           o.orderID, o.shipName
                    LIMIT 1""",
            """
                    MATCH (o:Order)-[r:CONTAINS]->(p:Product)
                    RETURN o.orderID, o.shipName,
                           p.productID, p.productName,
                           r.unitPrice, r.quantity, r.discount
                    LIMIT 1""",
            """
                    MATCH (o:Order)-[r:ORDERS]->(p:Product)
                    RETURN o.orderID, o.shipName,
                           p.productID, p.productName,
                           r.unitPrice, r.quantity, r.discount
                    LIMIT 1""",
            """
                    MATCH (p:Product)-[:PART_OF]->(c:Category)
                    RETURN p.productID, p.productName,
                           c.categoryID, c.categoryName
                    LIMIT 1""",
            """
                    MATCH (s:Shipper)-[:SHIPS]->(o:Order)
                    RETURN s.shipperID, s.companyName,
                           o.orderID, o.shipName
                    LIMIT 1""",
            """
                    MATCH (s:Supplier)-[:SUPPLIES]->(p:Product)
                    RETURN s.supplierID, s.companyName,
                           p.productID, p.productName
                    LIMIT 1""",
            """
                    MATCH (t:Territory)-[:IN_REGION]->(r:Region)
                    RETURN t.territoryID, t.territoryDescription,
                           r.regionID, r.regionDescription
                    LIMIT 1"""
    );
    /**
     *
     */
    public static final List<String> CYPHER_QUERIES = List.of(
            /*
        Pathfinding (Supply Chain Traceability)
        The Goal: Find the shortest operational path
            from a Supplier to a Customer who bought their products,
            bypassing standard table scans.
        Cypher uses the variable-length path operator '*..'
        to instantly traverse across different node labels.

        The Variable Length '(*..6)' means "Distance Budget".
        The *..6 syntax defines how many links (hops) the graph engine is allowed to traverse.
        The '*' means variable length.
        The '..6' means up to 6 hops maximum (e.g., 1, 2, 3, 4, 5, or 6 degrees of separation).
        Since it took only 3 hops, it easily fit within this budget.

        Instead of joining with a 5-table relational JOIN
            'Suppliers -> Products -> OrderDetails -> Orders -> Customers',
        Neo4j evaluates this as pointer hopping.
        The 'SHORTEST' stops executing the moment the destination node is hit.

        Undirected Traversal ('-' instead of '->') allows
        to traverse relationships regardless of their direction.
            */
            """
                    MATCH path = SHORTEST 1
                        (sup:Supplier {companyName: "Exotic Liquids"})
                        -[:SUPPLIES|PART_OF|ORDERS|SOLD|PURCHASED*..6]-
                        (cus:Customer {customerID: "ERNSH"})
                    RETURN path"""
    );
    /**
     * The API-Ready Query.
     */
    public static final String API_READY_QUERY_01 =
            """
                    MATCH (ord:Order {orderID: $id})
                    CALL (ord) {
                        MATCH (ord)-[:CONTAINS]->(prd)-[:PART_OF]->(cat)
                        OPTIONAL MATCH (prd)<-[:SUPPLIES]-(sup)
                        WITH prd, cat, sup ORDER BY prd.productID
                        RETURN collect({
                            productID: prd.productID,
                            productName: prd.productName,
                            category: cat {
                                .categoryID,
                                .categoryName
                            },
                            supplier: sup {
                                .supplierID,
                                .companyName
                            }
                        }) AS productContained
                    }
                    CALL (ord) {
                        MATCH (ord)-[:ORDERS]->(prd)-[:PART_OF]->(cat)
                        OPTIONAL MATCH (prd)<-[:SUPPLIES]-(sup)
                        WITH prd, cat, sup ORDER BY prd.productID
                        RETURN collect({
                            productID: prd.productID,
                            productName: prd.productName,
                            category: cat {
                                .categoryID,
                                .categoryName
                            },
                            supplier: sup {
                                .supplierID,
                                .companyName
                            }
                        }) AS productOrdered
                    }
                    CALL (ord) {
                        OPTIONAL MATCH (ord)<-[:SOLD]-(emp)
                        OPTIONAL MATCH path = (emp)-[:REPORTS_TO*0..]->(boss)
                        WHERE NOT EXISTS { (boss)-[:REPORTS_TO]->() }
                        OPTIONAL MATCH (emp)-[:IN_TERRITORY]->(ter)-[:IN_REGION]->(reg)
                        WITH emp, path, ter, reg ORDER BY ter.territoryID
                        RETURN
                            emp,
                            collect(DISTINCT ter {
                                .territoryID,
                                .territoryDescription,
                                region: reg {
                                    .regionID,
                                    .regionDescription
                                }
                            }) AS territories,
                            [master IN nodes(path)[1..] | master {
                                .employeeID,
                                .title,
                                fullName: master.firstName + ' ' + master.lastName
                            }] AS managementChain
                    }
                    WITH ord {
                        .orderID,
                        .shipName,
                        customer: [(ord)<-[:PURCHASED]-(cst) | cst {
                            .customerID,
                            .companyName
                        }][0],
                        shipper: [(ord)<-[:SHIPS]-(shp) | shp {
                            .shipperID,
                            .companyName
                        }][0],
                        productContained: productContained,
                        productOrdered: productOrdered,
                        employee: emp {
                            .employeeID,
                            .title,
                            fullName: emp.firstName + ' ' + emp.lastName,
                            territories: territories,
                            reportsTo: managementChain
                        }
                    } AS orders
                    RETURN COLLECT(orders) AS response
                    """;
    /**
     * The API-Ready Query.
     */
    public static final String API_READY_QUERY_02 =
            """
                    MATCH (sup:Supplier)
                    WITH sup ORDER BY sup.companyName
                    WITH sup {
                        .supplierID,
                        .companyName,
                        inventory: COLLECT {
                            MATCH (sup)-[:SUPPLIES]->(prd:Product)-[:PART_OF]->(cat:Category)
                            WITH cat, prd ORDER BY prd.productName
                            WITH cat, collect(prd {
                                .productID,
                                .productName
                            }) AS productNames
                            ORDER BY cat.categoryName
                            RETURN {
                                category: cat {
                                    .categoryID,
                                    .categoryName
                                },
                                products: productNames
                            }
                        }
                    } AS suppliers
                    RETURN collect(suppliers) AS response
                    """;
    /**
     * The API-Ready Query.
     */
    public static final String API_READY_QUERY_03 =
            """
                    MATCH (cat:Category)
                    WITH cat ORDER BY cat.categoryName
                    WITH cat {
                        .categoryID,
                        .categoryName,
                        inventory: COLLECT {
                            MATCH (cat)<-[:PART_OF]-(prd:Product)<-[:SUPPLIES]-(sup:Supplier)
                            WITH sup, prd ORDER BY prd.productName
                            WITH sup, collect(prd {
                                .productID,
                                .productName
                            }) AS productNames
                            ORDER BY sup.companyName
                            RETURN {
                                supplier: sup {
                                  .supplierID,
                                  .companyName
                                },
                                products: productNames
                            }
                        }
                    } AS categories
                    RETURN collect(categories) AS response
                    """;
    /**
     * The result of this Northwind dataset query is loaded into Java records.
     * Query uses Cypher Map Projection and Pattern Comprehension.
     * The Wildcard Map Projection syntax: 'node { .* }'.
     */
    public static final String GET_CUSTOMERS_QUERY = """
            UNWIND $customerIDs AS cusID
            MATCH (cus:Customer {customerID: cusID})
            RETURN cus {
                .*,
                purchasedOrders: [ (cus)-[:PURCHASED]->(ord:Order) | ord {
                    .*,
                    orderedProducts: [ (ord)-[rel:ORDERS]->(prd:Product)
                    WHERE prd.productID IN $productIDs | rel {
                        .*,
                        product: prd {
                            .*,
                            partOfCategories: [ (prd)-[:PART_OF]->(cat:Category) | cat { .* }]
                        }
                    }]
                }]
            } AS customerTree
            """;
    /**
     * Parameters for Northwind dataset query.
     */
    public static final Map<String, Object> GET_CUSTOMERS_QUERY_PARAMETERS = Map.of(
            "customerIDs", List.of("ALFKI", "ANATR", "ANTON", "AROUT", "BERGS"),
            "productIDs", List.of(2, 48, 33, 42, 13, 40, 59, 76, 46, 28)
    );
    /**
     * The result of this Northwind dataset query is loaded into Java records.
     */
    public static final String GET_ORDERS_QUERY = """
            MATCH (order:Order)<-[:PURCHASED]-(customer:Customer)
            MATCH (order)<-[:SHIPS]-(shipper:Shipper)
            MATCH (order)<-[:SOLD]-(employee:Employee)
            WHERE
            EXISTS { (employee)-[:REPORTS_TO]->(:Employee) } AND
            EXISTS { (employee)-[:IN_TERRITORY]->(:Territory)-[:IN_REGION]->(:Region) }
            RETURN order {
                .*,
                customer: customer { .* },
                shipper: shipper { .* },
                employee: employee {
                    .*,
                    managers: [(employee)-[:REPORTS_TO]->(m) | m { .* }],
                    territories: [(employee)-[:IN_TERRITORY]->(t) | t {
                        .*,
                        regions: [(t)-[:IN_REGION]->(r) | r { .* }]
                    }]
                },
                orderedProducts: [ (order)-[rel:ORDERS]->(prd:Product) | {
                    quantity: rel.quantity,
                    unitPrice: rel.unitPrice,
                    discount: rel.discount,
                    product: prd {
                        .*,
                        supplier: head([(prd)<-[:SUPPLIES]-(s) | s { .* }]),
                        partOfCategories: [(prd)-[:PART_OF]->(c) | c { .* }]
                    }
                }],
                containedProducts: [ (order)-[rel:CONTAINS]->(prd:Product) | {
                    quantity: rel.quantity,
                    unitPrice: rel.unitPrice,
                    discount: rel.discount,
                    product: prd {
                        .*,
                        supplier: head([(prd)<-[:SUPPLIES]-(s) | s { .* }]),
                        partOfCategories: [(prd)-[:PART_OF]->(c) | c { .* }]
                    }
                }]
            } AS ordersTree
            LIMIT 1
            """;

    /**
     * Private constructor to prevent instantiation.
     */
    private Neo4jConstants() {
        throw new IllegalStateException("Utility class");
    }
}
