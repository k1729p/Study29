package kp.web.httpserver;

/**
 * Constants for  web.
 */
public class WebConstants {
    /**
     * HTML fragment for the home page.
     */
    static final String HOME_PAGE = """
            <!DOCTYPE HTML>
            <html lang="en">
            <head>
            <title>Database Clients</title>
            <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
            <link rel="shortcut icon" href="favicon.ico" />
            <style>
            body {
              background-color: wheat;
            }
            table {
              width: 50%;
              border-collapse: collapse;
              border: 1px solid black;
            }
            caption {
              padding: 5px;
            }
            table td {
              padding: 3px;
              border: 1px solid black;
            }
            table tr:hover td {
              background: palegreen;
            }
            .col-1 {
              width: 20%
            }
            .col-2 {
              width: 80%
            }
            </style>
            </head>
            <body>
            <h1>Neo4j Cypher Queries</h1>
            <p>Queries on Northwind dataset.</p>
            <table>
            <tr>
            <td class="col-1">
                <a href='neo4j_query01'>1st Cypher query</a>
            </td><td class="col-2">
                Create a "star" graph around the Order node.
            </td></tr>
            <tr><td class="col-1">
                <a href='neo4j_query02'>2nd Cypher query</a>
            </td><td class="col-2">
                List the product <b>categories</b> provided by each <b>supplier</b>.<br>
                Supplier-centric context.
            </td></tr>
            <tr><td class="col-1">
                <a href='neo4j_query03'>3rd Cypher query</a>
            </td><td class="col-2">
                List the <b>suppliers</b> and their products for each <b>category</b>.<br>
                Category-centric context.
            </td></tr>
            </table>
             <hr>
            </body>
            </html>
            """;

    /**
     * Private constructor to prevent instantiation.
     */
    private WebConstants() {
        throw new IllegalStateException("Utility class");
    }
}
