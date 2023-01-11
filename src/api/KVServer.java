package api;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

public class KVServer {
    public static final int PORT = 8078;
    private final String apiToken;
    public static HttpServer server;
    private final Map<String, String> data = new HashMap<>();

    public KVServer() throws IOException {
        apiToken = generateApiToken();
        server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        server.createContext("/register", this::register);
        server.createContext("/save", this::save);
        server.createContext("/load", this::load);
    }

    private void load(HttpExchange httpExchange) throws IOException {
        try {
            String key = httpExchange.getRequestURI().getPath().substring("/load/".length());
            if (key.isEmpty()) {
                httpExchange.sendResponseHeaders(400, 0);
                String response = "Key для сохранения пустой. key указывается в пути: /load/{key}";
                try (OutputStream os = httpExchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
                return;
            }

            String response = data.get(key);
            if (response != null) {
                httpExchange.sendResponseHeaders(200, 0);
                try (OutputStream os = httpExchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
            } else {
                httpExchange.sendResponseHeaders(404, 0);
                response = "Значние key: " + key + " не найдено.";
                try (OutputStream os = httpExchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
            }
        } finally {
            httpExchange.close();
        }
    }

    private void save(HttpExchange httpExchange) throws IOException {
        try {
            if (!hasAuth(httpExchange)) {
                httpExchange.sendResponseHeaders(403, 0);
                String response = "Запрос неавторизован, нужен параметр в query API_TOKEN со значением апи-ключа";
                try (OutputStream os = httpExchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
                return;
            }

            if ("POST".equals(httpExchange.getRequestMethod())) {
                String key = httpExchange.getRequestURI().getPath().substring("/save/".length());
                if (key.isEmpty()) {
                    httpExchange.sendResponseHeaders(400, 0);
                    String response = "Key для сохранения пустой. key указывается в пути: /save/{key}";
                    try (OutputStream os = httpExchange.getResponseBody()) {
                        os.write(response.getBytes());
                    }
                    return;
                }
                String value = readText(httpExchange);
                if (value.isEmpty()) {
                    httpExchange.sendResponseHeaders(400, 0);
                    String response = "Value для сохранения пустой. value указывается в теле запроса";
                    try (OutputStream os = httpExchange.getResponseBody()) {
                        os.write(response.getBytes());
                    }
                    return;
                }
                data.put(key, value);
                httpExchange.sendResponseHeaders(200, 0);
                String response = "Значение для ключа " + key + " успешно обновлено!";
                try (OutputStream os = httpExchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
            } else {
                httpExchange.sendResponseHeaders(405, 0);
                String response = "/save ждёт POST-запрос, а получил: " + httpExchange.getRequestMethod();
                try (OutputStream os = httpExchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
            }
        } finally {
            httpExchange.close();
        }
    }

    private void register(HttpExchange httpExchange) throws IOException {
        try {
            if ("GET".equals(httpExchange.getRequestMethod())) {
                sendText(httpExchange, apiToken);
            } else {
                httpExchange.sendResponseHeaders(405, 0);
                String response = "/register ждёт GET-запрос, а получил " + httpExchange.getRequestMethod();
                try (OutputStream os = httpExchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
            }
        } finally {
            httpExchange.close();
        }
    }

    public void start() {
        System.out.println("KV-сервер запущен на " + PORT + " порту!");
        server.start();
    }

    public static void stop() {
        server.stop(1 / 100);
    }

    private String generateApiToken() {
        return "" + System.currentTimeMillis();
    }

    protected boolean hasAuth(HttpExchange httpExchange) {
        String rawQuery = httpExchange.getRequestURI().getRawQuery();
        return rawQuery != null && (rawQuery.contains("API_TOKEN=" + apiToken) || rawQuery.contains("API_TOKEN=DEBUG"));
    }

    protected String readText(HttpExchange httpExchange) throws IOException {
        return new String(httpExchange.getRequestBody().readAllBytes(), UTF_8);
    }

    protected void sendText(HttpExchange httpExchange, String text) throws IOException {
        byte[] resp = text.getBytes(UTF_8);
        httpExchange.getResponseHeaders().add("Content-Type", "application/json");
        httpExchange.sendResponseHeaders(200, resp.length);
        httpExchange.getResponseBody().write(resp);
    }
}

