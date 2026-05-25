package kp.web.httpserver;

import com.sun.net.httpserver.HttpExchange;
import kp.clients.neo4j.Neo4jClient;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;

import static kp.web.httpserver.WebConstants.HOME_PAGE;

/**
 * Handlers for the web server.
 */
public class WebHandlers {
    /**
     * Private constructor to prevent instantiation.
     */
    private WebHandlers() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Handles the 'Home' page.
     *
     * @param httpExchange the {@link HttpExchange}.
     */
    public static void handleHome(HttpExchange httpExchange) {

        try {
            handle(httpExchange, "text/html", HOME_PAGE);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }

    /**
     * Handles the Neo4j Cypher query.
     * <p>
     * The driver lifecycle.
     * Every time the 'processForWeb' method runs:
     * - driver is created with 'createConnectionPool' method
     * - driver is closed via the try-with-resources
     * This is very expensive!
     * The driver instance should be created once when the application starts and then reused.
     * </p>
     *
     * @param httpExchange the {@link HttpExchange}.
     * @param selector     the query selector.
     */
    public static void handleNeo4jQuery(HttpExchange httpExchange, int selector) {
        try {
            handle(httpExchange, "application/json", Neo4jClient.processForWeb(selector));
        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }

    /**
     * Handles the response.
     *
     * @param httpExchange the {@link HttpExchange}.
     * @param contentType  the content type.
     * @param response     the response.
     * @throws IOException if an I/O error occurs.
     */
    private static void handle(HttpExchange httpExchange, String contentType, String response)
            throws IOException {

        httpExchange.getResponseHeaders().set("Content-Type", contentType + "; charset=utf-8");
        final byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, bytes.length);

        try (OutputStream output = httpExchange.getResponseBody()) {
            output.write(bytes);
            output.flush();
        }
        httpExchange.close();
    }
}
