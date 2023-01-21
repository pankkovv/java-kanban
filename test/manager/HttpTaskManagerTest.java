package manager;

import api.KVServer;
import com.google.gson.Gson;

import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HttpTaskManagerTest {
    Gson gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new LocalDateAdapter()).create();

    public class LocalDateAdapter extends TypeAdapter<LocalDateTime> {
        private final DateTimeFormatter formatterWriter = DateTimeFormatter.ofPattern("yyyy-MM-dd|HH:mm:ss");
        private final DateTimeFormatter formatterReader = DateTimeFormatter.ofPattern("yyyy-MM-dd|HH:mm:ss");

        @Override
        public void write(final JsonWriter jsonWriter, final LocalDateTime localDateTime) throws IOException {
            jsonWriter.value(localDateTime.format(formatterWriter));
        }

        @Override
        public LocalDateTime read(final JsonReader jsonReader) throws IOException {
            return LocalDateTime.parse(jsonReader.nextString(), formatterReader);
        }
    }

    HttpTaskManager manager;
    HttpClient client;
    HttpResponse.BodyHandler<String> handler;

    URI urlTask;
    URI urlEpic;
    URI urlSub;
    URI urlHistory;


    @BeforeEach
    public void startServer() throws IOException, InterruptedException {
        manager = new HttpTaskManager("http://localhost:8078");
        handler = HttpResponse.BodyHandlers.ofString();
        urlTask = URI.create("http://localhost:8078/load/task");
        urlEpic = URI.create("http://localhost:8078/load/epic");
        urlSub = URI.create("http://localhost:8078/load/subtask");
        urlHistory = URI.create("http://localhost:8078/load/history");
        client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();
    }

    @AfterEach
    public void stopServer() throws IOException {
        KVServer.stop();
    }

    //Со стандартным поведением.
    @Test
    public void saveAndLoadTest() throws IOException, InterruptedException {
        Task task = manager.createTask("a", "a", "DONE");
        Epic epic = manager.createEpic("b", "b", "DONE");
        Subtask sub = manager.createSubtask(epic.getId(), "c", "c", "DONE");
        manager.getTaskId(task.getId());
        manager.getEpicId(epic.getId());
        manager.getSubtaskId(sub.getId());
        manager.getHistory();

        HttpResponse<String> responseTask = client.send(HttpRequest.newBuilder().GET().uri(urlTask).version(HttpClient.Version.HTTP_1_1).build(), handler);
        HttpResponse<String> responseEpic = client.send(HttpRequest.newBuilder().GET().uri(urlEpic).version(HttpClient.Version.HTTP_1_1).build(), handler);
        HttpResponse<String> responseSub = client.send(HttpRequest.newBuilder().GET().uri(urlSub).version(HttpClient.Version.HTTP_1_1).build(), handler);
        HttpResponse<String> responseHistory = client.send(HttpRequest.newBuilder().GET().uri(urlHistory).version(HttpClient.Version.HTTP_1_1).build(), handler);

        assertEquals(gson.toJson(manager.getTask()), responseTask.body());
        assertEquals(gson.toJson(manager.getEpic()), responseEpic.body());
        assertEquals(gson.toJson(manager.getSubtask()), responseSub.body());
        assertEquals(gson.toJson(manager.getHistory()), responseHistory.body());
    }

    //Пустой список задач.
    @Test
    public void saveAndLoadEmptyTest() throws IOException, InterruptedException {
        HttpResponse<String> responseTask = client.send(HttpRequest.newBuilder().GET().uri(urlTask).version(HttpClient.Version.HTTP_1_1).build(), handler);
        HttpResponse<String> responseEpic = client.send(HttpRequest.newBuilder().GET().uri(urlEpic).version(HttpClient.Version.HTTP_1_1).build(), handler);
        HttpResponse<String> responseSub = client.send(HttpRequest.newBuilder().GET().uri(urlSub).version(HttpClient.Version.HTTP_1_1).build(), handler);
        HttpResponse<String> responseHistory = client.send(HttpRequest.newBuilder().GET().uri(urlHistory).version(HttpClient.Version.HTTP_1_1).build(), handler);

        assertEquals("Значние key: task не найдено.", responseTask.body());
        assertEquals("Значние key: epic не найдено.", responseEpic.body());
        assertEquals("Значние key: subtask не найдено.", responseSub.body());
        assertEquals("Значние key: history не найдено.", responseHistory.body());
    }

}