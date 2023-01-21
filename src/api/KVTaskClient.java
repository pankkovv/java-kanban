package api;


import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class KVTaskClient {

    private static URI url;
    private static String API_TOKEN;

    static HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    static HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();

    public KVTaskClient(String url) throws IOException, InterruptedException {
        new KVServer().start();
        this.url = URI.create(url);
        this.API_TOKEN = register();
    }

    private String register() throws IOException, InterruptedException {
        URI authURL = URI.create(url + "/register");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(authURL)
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        HttpResponse<String> response = client.send(request, handler);
        System.out.println("API_TOKEN: " + response.body());
        return response.body();
    }

    public void put(String key, String json) throws IOException, InterruptedException {
        URI urlPut;
        if (key.endsWith("%3F")) {
            urlPut = URI.create(url + "/save/" + key + "&API_TOKEN=" + API_TOKEN);
        } else {
            urlPut = URI.create(url + "/save/" + key + "?API_TOKEN=" + API_TOKEN);
        }
        HttpRequest requestPut = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(urlPut)
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        HttpResponse<String> responsePut = client.send(requestPut, handler);
    }

    public String load(String key) throws IOException, InterruptedException {
        URI urlPut;
        if (key.endsWith("%3F")) {
            urlPut = URI.create(url + "/load/" + key + "&API_TOKEN=" + API_TOKEN);
        } else {
            urlPut = URI.create(url + "/load/" + key + "?API_TOKEN=" + API_TOKEN);
        }
        HttpRequest requestLoad = HttpRequest.newBuilder()
                .GET()
                .uri(urlPut)
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        HttpResponse<String> responseLoad = client.send(requestLoad, handler);

        if (responseLoad.statusCode() == 200) {
            return responseLoad.body();
        } else if (responseLoad.statusCode() == 400) {
            return responseLoad.body();
        } else {
            return responseLoad.body();
        }
    }
}
