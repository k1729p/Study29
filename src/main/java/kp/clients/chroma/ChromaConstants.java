package kp.clients.chroma;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
import kp.utils.Tools;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Constants for Chroma database.
 */
public class ChromaConstants {
    static final HttpClient HTTP_CLIENT = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).build();
    private static final String BASE_URL = String.format(
            "http://%s:%d/api/v2/tenants/default_tenant/databases/default_database/collections",
            Tools.getEnvStrOrDefault("CHROMA_HOST", "localhost"),
            Tools.getEnvIntOrDefault("CHROMA_PORT", 8000));
    static final HttpRequest COLLECTIONS_REQUEST =
            HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL))
                    .GET()
                    .header("Accept", "application/json")
                    .build();
    static final Function<String, HttpRequest> RECORDS_REQUEST_FUN = collectionId ->
            HttpRequest.newBuilder()
                    .uri(URI.create(String.format("%s/%s/get", BASE_URL, collectionId)))
                    .POST(HttpRequest.BodyPublishers.ofString("{}"))
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .build();

    static final JsonMapper JSON_MAPPER = new JsonMapper();
    static final BiFunction<JsonNode, String, Optional<JsonNode>> JSON_NODE_FUN =
            (jsonNode, label) ->
                    Optional.ofNullable(jsonNode).map(node -> node.get(label)).filter(JsonNode::isArray);
    static final BiFunction<Optional<JsonNode>, Integer, Integer> NODE_TO_INT_FUN =
            (nodeOpt, index) ->
                    nodeOpt.map(node -> node.get(index)).filter(Predicate.not(JsonNode::isNull))
                            .map(JsonNode::asText).map(Integer::parseInt).orElse(0);
    static final BiFunction<Optional<JsonNode>, Integer, String> NODE_TO_STR_FUN =
            (nodeOpt, index) ->
                    nodeOpt.map(node -> node.get(index)).filter(Predicate.not(JsonNode::isNull))
                            .map(JsonNode::asText).orElse("");
    static final BiFunction<Optional<JsonNode>, String, Integer> NODE_P_TO_INT_FUN =
            (nodeOpt, label) ->
                    nodeOpt.map(node -> node.path(label)).map(JsonNode::asInt).orElse(0);
    static final BiFunction<Optional<JsonNode>, String, String> NODE_P_TO_STR_FUN =
            (nodeOpt, label) ->
                    nodeOpt.map(node -> node.path(label)).map(JsonNode::asText).orElse("");

    static final TypeReference<List<Map<String, Object>>> COL_TYPE_REFERENCE = new TypeReference<>() {
    };

    /**
     * Private constructor to prevent instantiation.
     */
    private ChromaConstants() {
        throw new IllegalStateException("Utility class");
    }
}