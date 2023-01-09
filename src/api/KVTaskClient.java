package api;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import static jdk.internal.util.xml.XMLStreamWriter.DEFAULT_CHARSET;

public class KVTaskClient {
    private static URI url;
    public static String API_TOKEN;

    static HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    static HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
    public KVTaskClient(String url) throws IOException, InterruptedException {
        this.url = URI.create(url);
        this.API_TOKEN = register();
    }

    private String register() throws IOException, InterruptedException {
        URI authURL = URI.create(url + "register");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(authURL)
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        HttpResponse<String> response = client.send(request, handler);
        System.out.println("API_TOKEN: " + response.body());
        return response.body();
    }

    public static void put(String key, String json) throws IOException, InterruptedException {
        URI urlPut = URI.create(url + "save/" + key + "?API_TOKEN=" + API_TOKEN);
        HttpRequest requestPut = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(urlPut)
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        HttpResponse<String> responsePut = client.send(requestPut, handler);
    }

    public static String load(String key) throws IOException, InterruptedException {
        URI urlPut = URI.create(url + "load/" + key + "?API_TOKEN=" + API_TOKEN);
        HttpRequest requestLoad = HttpRequest.newBuilder()
                .GET()
                .uri(urlPut)
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        HttpResponse<String> responsePut = client.send(requestLoad, handler);

        if(responsePut.statusCode() == 200){
            return responsePut.body();
        } else if(responsePut.statusCode() == 400) {
            return "| 400 | Key для сохранения пустой. key указывается в пути: /load/{key}";
        } else {
            return "| 404 | Значние key: " + key + " не найдено.";
        }
    }
}
