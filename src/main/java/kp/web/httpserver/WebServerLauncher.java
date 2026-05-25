package kp.web.httpserver;

import com.sun.net.httpserver.HttpServer;

import java.awt.*;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.stream.IntStream;

/**
 * The launcher for the {@link HttpServer}.
 */
public class WebServerLauncher {
    /**
     * The {@link HttpServer}.
     */
    private static HttpServer httpServer;
    /**
     * The {@link HttpServer} server port.
     */
    private static final int PORT = 8181;
    /**
     * The server home URL.
     */
    private static final String HOME_URL = String.format("http://localhost:%d", PORT);

    /**
     * Private constructor to prevent instantiation.
     */
    private WebServerLauncher() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * The primary entry point for launching the web server.
     */
    static void main() {

        if (!Desktop.isDesktopSupported()) {
            System.out.println("Desktop not supported!");
            System.exit(1);
        }
        /*-
         * Web server is started in a new background thread.
         */
        startServer();
        try {
            Desktop.getDesktop().browse(new URI(HOME_URL));
        } catch (URISyntaxException | IOException e) {
            System.out.println(e.getMessage());
            httpServer.stop(0);
            System.exit(1);
        }
    }

    /**
     * Starts the {@link HttpServer}.
     */
    public static void startServer() {

        try {
            httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
        httpServer.createContext("/", WebHandlers::handleHome);
        IntStream.rangeClosed(1, 3).forEach(num -> httpServer.createContext(
                String.format("/neo4j_query%02d", num),
                httpExchange -> WebHandlers.handleNeo4jQuery(httpExchange, num)));
        httpServer.start();
        System.out.println("Web server started");
        System.out.println("- ".repeat(50));
    }
}