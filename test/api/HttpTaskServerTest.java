package api;

import com.google.gson.Gson;

import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import manager.InMemoryTaskManager;
import manager.TaskManager;

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


class HttpTaskServerTest {

    Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateAdapter())
            .create();

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

    HttpTaskServer manager;

    TaskManager managerTask;

    HttpClient client;
    HttpResponse.BodyHandler<String> handler;

    URI urlSave;
    URI urlLoad;
    URI urlTasks;
    URI urlTask;
    URI urlEpic;
    URI urlSub;
    URI urlTaskId;
    URI urlEpicId;
    URI urlSubId;
    URI urlAllsub;
    URI urlHistory;
    URI urlUpTask;
    URI urlUpEpic;
    URI urlUpSub;

    Task task;
    Epic epic;
    Subtask sub;


    @BeforeEach
    public void startServer() throws IOException, InterruptedException {
        managerTask = new InMemoryTaskManager();
        manager = new HttpTaskServer();
        manager.start();
        handler = HttpResponse.BodyHandlers.ofString();
        client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();

        urlSave = URI.create("http://localhost:8080/save");
        urlLoad = URI.create("http://localhost:8080/load");
        urlTasks = URI.create("http://localhost:8080/tasks");
        urlTask = URI.create("http://localhost:8080/tasks/task");
        urlEpic = URI.create("http://localhost:8080/tasks/epic");
        urlSub = URI.create("http://localhost:8080/tasks/subtask");
        urlTaskId = URI.create("http://localhost:8080/tasks/task/%3Fid=");
        urlEpicId = URI.create("http://localhost:8080/tasks/epic/%3Fid=");
        urlSubId = URI.create("http://localhost:8080/tasks/subtask/%3Fid=");
        urlAllsub = URI.create("http://localhost:8080/tasks/allsubepic/%3Fid=");
        urlHistory = URI.create("http://localhost:8080/tasks/history");
        urlUpTask = URI.create("http://localhost:8080/tasks/uptask/%3Fid=");
        urlUpEpic = URI.create("http://localhost:8080/tasks/upepic/%3Fid=");
        urlUpSub = URI.create("http://localhost:8080/tasks/upsubtask/%3Fid=");
    }

    @BeforeEach
    public void assistant() throws IOException, InterruptedException {
        task = managerTask.createTask("a", "a", "DONE");
        epic = managerTask.createEpic("b", "b", "DONE");
        sub = managerTask.createSubtask(epic.getId(), "c", "c", "DONE");
        managerTask.getTaskId(task.getId());
        managerTask.getEpicId(epic.getId());
        managerTask.getSubtaskId(sub.getId());

        HttpResponse<String> responseTaskPost = client.send(HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                .uri(urlTask)
                .version(HttpClient.Version.HTTP_1_1)
                .build(), handler);

        HttpResponse<String> responseEpic = client.send(HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .uri(urlEpic)
                .version(HttpClient.Version.HTTP_1_1)
                .build(), handler);

        HttpResponse<String> responseSub = client.send(HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(sub)))
                .uri(URI.create(urlSub.toString() + "/%3Fid=100"))
                .version(HttpClient.Version.HTTP_1_1)
                .build(), handler);
    }

    @AfterEach
    public void stopServer() throws IOException {
        KVServer.stop();
        HttpTaskServer.stop();
    }

    //Test load method
    @Test
    public void loadNormalTest() throws IOException, InterruptedException {
        HttpResponse<String> responseTaskLoad = client.send(HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(urlLoad.toString() + "/task%3Fid=0"))
                .version(HttpClient.Version.HTTP_1_1)
                .build(), handler);

        HttpResponse<String> responseEpicLoad = client.send(HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(urlLoad.toString() + "/epic%3Fid=100"))
                .version(HttpClient.Version.HTTP_1_1)
                .build(), handler);

        HttpResponse<String> responseSubLoad = client.send(HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(urlLoad.toString() + "/subtask%3Fid=200"))
                .version(HttpClient.Version.HTTP_1_1)
                .build(), handler);

        HttpResponse<String> responseHistoryLoad = client.send(HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(urlLoad.toString() + "/history"))
                .version(HttpClient.Version.HTTP_1_1)
                .build(), handler);

        assertEquals("[" + gson.toJson(managerTask.getTaskId(task.getId())) + "]", responseTaskLoad.body());
        assertEquals("[" + gson.toJson(managerTask.getSubtaskId(sub.getId())) + "]", responseSubLoad.body());
        assertEquals("[" + gson.toJson(managerTask.getEpicId(epic.getId())) + "]", responseEpicLoad.body());
        assertEquals("[]", responseHistoryLoad.body());
    }

    @Test
    public void loadNormalEmptyTest() throws IOException, InterruptedException {
        HttpResponse<String> responseTaskDelete = client.send(HttpRequest.newBuilder()
                .DELETE()
                .uri(urlTask)
                .version(HttpClient.Version.HTTP_1_1)
                .build(), handler);
        HttpResponse<String> responseEpicDelete = client.send(HttpRequest.newBuilder()
                .DELETE()
                .uri(urlEpic)
                .version(HttpClient.Version.HTTP_1_1)
                .build(), handler);
        HttpResponse<String> responseSubDelete = client.send(HttpRequest.newBuilder()
                .DELETE()
                .uri(urlSub)
                .version(HttpClient.Version.HTTP_1_1)
                .build(), handler);

        HttpResponse<String> responseTaskLoad = client.send(HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(urlLoad.toString() + "/task%3Fid=0"))
                .version(HttpClient.Version.HTTP_1_1)
                .build(), handler);

        HttpResponse<String> responseEpicLoad = client.send(HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(urlLoad.toString() + "/epic%3Fid=100"))
                .version(HttpClient.Version.HTTP_1_1)
                .build(), handler);

        HttpResponse<String> responseSubLoad = client.send(HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(urlLoad.toString() + "/subtask%3Fid=200"))
                .version(HttpClient.Version.HTTP_1_1)
                .build(), handler);

        HttpResponse<String> responseHistoryLoad = client.send(HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(urlLoad.toString() + "/history"))
                .version(HttpClient.Version.HTTP_1_1)
                .build(), handler);

        assertEquals("[" + gson.toJson(managerTask.getTaskId(task.getId())) + "]", responseTaskLoad.body());
        assertEquals("[" + gson.toJson(managerTask.getSubtaskId(sub.getId())) + "]", responseSubLoad.body());
        assertEquals("[" + gson.toJson(managerTask.getEpicId(epic.getId())) + "]", responseEpicLoad.body());
        assertEquals("[]", responseHistoryLoad.body());
    }

    //Со стандартным поведением.
    @Test
    public void createAndGetTaskTest() throws IOException, InterruptedException {
        HttpResponse<String> responseTaskGet = client.send(HttpRequest.newBuilder()
                .GET()
                .uri(urlTask)
                .version(HttpClient.Version.HTTP_1_1)
                .build(), handler);

        HttpResponse<String> responseSubGet = client.send(HttpRequest.newBuilder()
                .GET()
                .uri(urlSub)
                .version(HttpClient.Version.HTTP_1_1)
                .build(), handler);

        HttpResponse<String> responseEpicGet = client.send(HttpRequest.newBuilder()
                .GET()
                .uri(urlEpic)
                .version(HttpClient.Version.HTTP_1_1)
                .build(), handler);

        assertEquals(gson.toJson(managerTask.getTask()), responseTaskGet.body());
        assertEquals(gson.toJson(managerTask.getEpic()), responseEpicGet.body());
        assertEquals(gson.toJson(managerTask.getSubtask()), responseSubGet.body());
    }

    @Test
    public void getIdTaskTest() throws IOException, InterruptedException {
        HttpResponse<String> responseTaskGetId = client.send(HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(urlTaskId.toString() + "0"))
                .version(HttpClient.Version.HTTP_1_1)
                .build(), handler);

        HttpResponse<String> responseEpicGetId = client.send(HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(urlEpicId.toString() + "100"))
                .version(HttpClient.Version.HTTP_1_1)
                .build(), handler);

        HttpResponse<String> responseSubGetId = client.send(HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(urlSubId.toString() + "200"))
                .version(HttpClient.Version.HTTP_1_1)
                .build(), handler);

        assertEquals("Задача #0:\n" + gson.toJson(managerTask.getTaskId(task.getId())), responseTaskGetId.body());
        assertEquals("Задача #100:\n" + gson.toJson(managerTask.getEpicId(epic.getId())), responseEpicGetId.body());
        assertEquals("Задача #200:\n" + gson.toJson(managerTask.getSubtaskId(sub.getId())), responseSubGetId.body());
    }

    @Test
    public void getAllSubEpicTest() throws IOException, InterruptedException {
        HttpResponse<String> responseAllSubGet = client.send(HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(urlAllsub.toString() + "100"))
                .version(HttpClient.Version.HTTP_1_1)
                .build(), handler);

        assertEquals(gson.toJson(managerTask.getListAllSubtask(epic.getId())), responseAllSubGet.body());
    }

    @Test
    public void getPriorityTest() throws IOException, InterruptedException {
        HttpResponse<String> responsePriority = client.send(HttpRequest.newBuilder()
                .GET()
                .uri(urlTasks)
                .version(HttpClient.Version.HTTP_1_1)
                .build(), handler);

        assertEquals(gson.toJson(managerTask.getPrioritizedTasks()), responsePriority.body());
    }

    @Test
    public void getHistoryTest() throws IOException, InterruptedException {
        HttpResponse<String> responseTaskGetId = client.send(HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(urlTaskId.toString() + "0"))
                .version(HttpClient.Version.HTTP_1_1)
                .build(), handler);

        HttpResponse<String> responseEpicGetId = client.send(HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(urlEpicId.toString() + "100"))
                .version(HttpClient.Version.HTTP_1_1)
                .build(), handler);

        HttpResponse<String> responseSubGetId = client.send(HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(urlSubId.toString() + "200"))
                .version(HttpClient.Version.HTTP_1_1)
                .build(), handler);

        HttpResponse<String> responseHistoryGet = client.send(HttpRequest.newBuilder()
                .GET()
                .uri(urlHistory)
                .version(HttpClient.Version.HTTP_1_1)
                .build(), handler);

        assertEquals(gson.toJson(managerTask.getHistory()), responseHistoryGet.body());
    }

    @Test
    public void updateTaskTest() throws IOException, InterruptedException {
        managerTask.updateTask(task.getId(), "e", "e", "DONE");
        managerTask.updateEpic(epic.getId(), "e", "e");
        managerTask.updateSubtask(epic.getId(), sub.getId(), "e", "e", "DONE");

        HttpResponse<String> responseUpdateTask = client.send(HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                .uri(URI.create(urlUpTask.toString() + "0"))
                .version(HttpClient.Version.HTTP_1_1)
                .build(), handler);

        HttpResponse<String> responseUpdateSub = client.send(HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(sub)))
                .uri(URI.create(urlUpSub.toString() + "100&id=200"))
                .version(HttpClient.Version.HTTP_1_1)
                .build(), handler);

        HttpResponse<String> responseUpdateEpic = client.send(HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .uri(URI.create(urlUpEpic.toString() + "100"))
                .version(HttpClient.Version.HTTP_1_1)
                .build(), handler);

        HttpResponse<String> responseTaskGetId = client.send(HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(urlTaskId.toString() + "0"))
                .version(HttpClient.Version.HTTP_1_1)
                .build(), handler);

        HttpResponse<String> responseEpicGetId = client.send(HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(urlEpicId.toString() + "100"))
                .version(HttpClient.Version.HTTP_1_1)
                .build(), handler);

        HttpResponse<String> responseSubGetId = client.send(HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(urlSubId.toString() + "200"))
                .version(HttpClient.Version.HTTP_1_1)
                .build(), handler);

        assertEquals("Задача #0:\n" + gson.toJson(managerTask.getTaskId(task.getId())), responseTaskGetId.body());
        assertEquals("Задача #100:\n" + gson.toJson(managerTask.getEpicId(epic.getId())), responseEpicGetId.body());
        assertEquals("Задача #200:\n" + gson.toJson(managerTask.getSubtaskId(sub.getId())), responseSubGetId.body());
    }

    @Test
    public void deleteTaskTest() throws IOException, InterruptedException {
        managerTask.removeTask();

        HttpResponse<String> responseTaskDelete = client.send(HttpRequest.newBuilder()
                .DELETE()
                .uri(urlTask)
                .version(HttpClient.Version.HTTP_1_1)
                .build(), handler);

        HttpResponse<String> responseTaskGet = client.send(HttpRequest.newBuilder()
                .GET()
                .uri(urlTask)
                .version(HttpClient.Version.HTTP_1_1)
                .build(), handler);

        HttpResponse<String> responseEpicDelete = client.send(HttpRequest.newBuilder()
                .DELETE()
                .uri(urlEpic)
                .version(HttpClient.Version.HTTP_1_1)
                .build(), handler);

        HttpResponse<String> responseEpicGet = client.send(HttpRequest.newBuilder()
                .GET()
                .uri(urlEpic)
                .version(HttpClient.Version.HTTP_1_1)
                .build(), handler);

        HttpResponse<String> responseSubDelete = client.send(HttpRequest.newBuilder()
                .DELETE()
                .uri(urlSub)
                .version(HttpClient.Version.HTTP_1_1)
                .build(), handler);

        HttpResponse<String> responseSubGet = client.send(HttpRequest.newBuilder()
                .GET()
                .uri(urlSub)
                .version(HttpClient.Version.HTTP_1_1)
                .build(), handler);

        assertEquals("Задачи удалены.", responseTaskDelete.body());
        assertEquals("[]", responseTaskGet.body());

        assertEquals("Задачи удалены.", responseEpicDelete.body());
        assertEquals("[]", responseEpicGet.body());

        assertEquals("Задачи удалены.", responseSubDelete.body());
        assertEquals("[]", responseSubGet.body());
    }

    @Test
    public void deleteTaskIdTest() throws IOException, InterruptedException {
        managerTask.removeTaskId(task.getId());
        managerTask.removeEpicId(epic.getId());
        managerTask.removeSubtaskId(sub.getId());

        HttpResponse<String> responseTaskDelete = client.send(HttpRequest.newBuilder()
                .DELETE()
                .uri(URI.create(urlTaskId.toString() + "0"))
                .version(HttpClient.Version.HTTP_1_1)
                .build(), handler);

        HttpResponse<String> responseTaskGetId = client.send(HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(urlTaskId.toString() + "0"))
                .version(HttpClient.Version.HTTP_1_1)
                .build(), handler);

        HttpResponse<String> responseEpicDelete = client.send(HttpRequest.newBuilder()
                .DELETE()
                .uri(URI.create(urlEpicId.toString() + "100"))
                .version(HttpClient.Version.HTTP_1_1)
                .build(), handler);

        HttpResponse<String> responseEpicGetId = client.send(HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(urlEpicId.toString() + "100"))
                .version(HttpClient.Version.HTTP_1_1)
                .build(), handler);

        HttpResponse<String> responseSubDelete = client.send(HttpRequest.newBuilder()
                .DELETE()
                .uri(URI.create(urlSubId.toString() + "200"))
                .version(HttpClient.Version.HTTP_1_1)
                .build(), handler);

        HttpResponse<String> responseSubGetId = client.send(HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(urlSubId.toString() + "200"))
                .version(HttpClient.Version.HTTP_1_1)
                .build(), handler);

        assertEquals("Задача с идентификатором 0 не найдена", responseTaskGetId.body());
        assertEquals("Задача с идентификатором 100 не найдена", responseEpicGetId.body());
        assertEquals("Задача с идентификатором 200 не найдена", responseSubGetId.body());

        assertEquals("Задача удалена.", responseTaskDelete.body());
        assertEquals("Задача удалена.", responseEpicDelete.body());
        assertEquals("Задача удалена.", responseSubDelete.body());
    }

    //С пустым списком задач.
    @Test
    public void createAndGetTaskEmptyTest() throws IOException, InterruptedException {
        HttpResponse<String> responseTaskDelete = client.send(HttpRequest.newBuilder()
                .DELETE()
                .uri(urlTask)
                .version(HttpClient.Version.HTTP_1_1)
                .build(), handler);
        HttpResponse<String> responseEpicDelete = client.send(HttpRequest.newBuilder()
                .DELETE()
                .uri(urlEpic)
                .version(HttpClient.Version.HTTP_1_1)
                .build(), handler);
        HttpResponse<String> responseSubDelete = client.send(HttpRequest.newBuilder()
                .DELETE()
                .uri(urlSub)
                .version(HttpClient.Version.HTTP_1_1)
                .build(), handler);


        HttpResponse<String> responseTaskGet = client.send(HttpRequest.newBuilder()
                .GET()
                .uri(urlTask)
                .version(HttpClient.Version.HTTP_1_1)
                .build(), handler);

        HttpResponse<String> responseSubGet = client.send(HttpRequest.newBuilder()
                .GET()
                .uri(urlSub)
                .version(HttpClient.Version.HTTP_1_1)
                .build(), handler);

        HttpResponse<String> responseEpicGet = client.send(HttpRequest.newBuilder()
                .GET()
                .uri(urlEpic)
                .version(HttpClient.Version.HTTP_1_1)
                .build(), handler);

        assertEquals("[]", responseTaskGet.body());
        assertEquals("[]", responseEpicGet.body());
        assertEquals("[]", responseSubGet.body());
    }

    @Test
    public void getIdTaskEmptyTest() throws IOException, InterruptedException {
        HttpResponse<String> responseTaskDelete = client.send(HttpRequest.newBuilder()
                .DELETE()
                .uri(urlTask)
                .version(HttpClient.Version.HTTP_1_1)
                .build(), handler);
        HttpResponse<String> responseEpicDelete = client.send(HttpRequest.newBuilder()
                .DELETE()
                .uri(urlEpic)
                .version(HttpClient.Version.HTTP_1_1)
                .build(), handler);
        HttpResponse<String> responseSubDelete = client.send(HttpRequest.newBuilder()
                .DELETE()
                .uri(urlSub)
                .version(HttpClient.Version.HTTP_1_1)
                .build(), handler);

        HttpResponse<String> responseTaskGetId = client.send(HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(urlTaskId.toString() + "0"))
                .version(HttpClient.Version.HTTP_1_1)
                .build(), handler);

        HttpResponse<String> responseEpicGetId = client.send(HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(urlEpicId.toString() + "100"))
                .version(HttpClient.Version.HTTP_1_1)
                .build(), handler);

        HttpResponse<String> responseSubGetId = client.send(HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(urlSubId.toString() + "200"))
                .version(HttpClient.Version.HTTP_1_1)
                .build(), handler);

        assertEquals("Задача с идентификатором 0 не найдена", responseTaskGetId.body());
        assertEquals("Задача с идентификатором 100 не найдена", responseEpicGetId.body());
        assertEquals("Задача с идентификатором 200 не найдена", responseSubGetId.body());
    }

    @Test
    public void getAllSubEpicEmptyTest() throws IOException, InterruptedException {
        HttpResponse<String> responseTaskDelete = client.send(HttpRequest.newBuilder()
                .DELETE()
                .uri(urlTask)
                .version(HttpClient.Version.HTTP_1_1)
                .build(), handler);
        HttpResponse<String> responseEpicDelete = client.send(HttpRequest.newBuilder()
                .DELETE()
                .uri(urlEpic)
                .version(HttpClient.Version.HTTP_1_1)
                .build(), handler);
        HttpResponse<String> responseSubDelete = client.send(HttpRequest.newBuilder()
                .DELETE()
                .uri(urlSub)
                .version(HttpClient.Version.HTTP_1_1)
                .build(), handler);

        HttpResponse<String> responseAllSubGet = client.send(HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(urlAllsub.toString() + "100"))
                .version(HttpClient.Version.HTTP_1_1)
                .build(), handler);

        assertEquals("[]", responseAllSubGet.body());
    }

    @Test
    public void getPriorityEmptyTest() throws IOException, InterruptedException {
        HttpResponse<String> responseTaskDelete = client.send(HttpRequest.newBuilder()
                .DELETE()
                .uri(urlTask)
                .version(HttpClient.Version.HTTP_1_1)
                .build(), handler);
        HttpResponse<String> responseSubDelete = client.send(HttpRequest.newBuilder()
                .DELETE()
                .uri(urlSub)
                .version(HttpClient.Version.HTTP_1_1)
                .build(), handler);

        HttpResponse<String> responsePriority = client.send(HttpRequest.newBuilder()
                .GET()
                .uri(urlTasks)
                .version(HttpClient.Version.HTTP_1_1)
                .build(), handler);

        assertEquals("[]", responsePriority.body());
    }

    @Test
    public void getHistoryEmptyTest() throws IOException, InterruptedException {
        HttpResponse<String> responseTaskDelete = client.send(HttpRequest.newBuilder()
                .DELETE()
                .uri(urlTask)
                .version(HttpClient.Version.HTTP_1_1)
                .build(), handler);
        HttpResponse<String> responseEpicDelete = client.send(HttpRequest.newBuilder()
                .DELETE()
                .uri(urlEpic)
                .version(HttpClient.Version.HTTP_1_1)
                .build(), handler);
        HttpResponse<String> responseSubDelete = client.send(HttpRequest.newBuilder()
                .DELETE()
                .uri(urlSub)
                .version(HttpClient.Version.HTTP_1_1)
                .build(), handler);

        HttpResponse<String> responseTaskGetId = client.send(HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(urlTaskId.toString() + "0"))
                .version(HttpClient.Version.HTTP_1_1)
                .build(), handler);

        HttpResponse<String> responseEpicGetId = client.send(HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(urlEpicId.toString() + "100"))
                .version(HttpClient.Version.HTTP_1_1)
                .build(), handler);

        HttpResponse<String> responseSubGetId = client.send(HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(urlSubId.toString() + "200"))
                .version(HttpClient.Version.HTTP_1_1)
                .build(), handler);

        HttpResponse<String> responseHistoryGet = client.send(HttpRequest.newBuilder()
                .GET()
                .uri(urlHistory)
                .version(HttpClient.Version.HTTP_1_1)
                .build(), handler);

        assertEquals("[]", responseHistoryGet.body());
    }

    @Test
    public void updateTaskEmptyTest() throws IOException, InterruptedException {
        HttpResponse<String> responseTaskDelete = client.send(HttpRequest.newBuilder()
                .DELETE()
                .uri(urlTask)
                .version(HttpClient.Version.HTTP_1_1)
                .build(), handler);
        HttpResponse<String> responseEpicDelete = client.send(HttpRequest.newBuilder()
                .DELETE()
                .uri(urlEpic)
                .version(HttpClient.Version.HTTP_1_1)
                .build(), handler);
        HttpResponse<String> responseSubDelete = client.send(HttpRequest.newBuilder()
                .DELETE()
                .uri(urlSub)
                .version(HttpClient.Version.HTTP_1_1)
                .build(), handler);

        managerTask.updateTask(task.getId(), "e", "e", "DONE");
        managerTask.updateEpic(epic.getId(), "e", "e");
        managerTask.updateSubtask(epic.getId(), sub.getId(), "e", "e", "DONE");

        HttpResponse<String> responseUpdateTask = client.send(HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                .uri(URI.create(urlUpTask.toString() + "0"))
                .version(HttpClient.Version.HTTP_1_1)
                .build(), handler);

        HttpResponse<String> responseUpdateSub = client.send(HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(sub)))
                .uri(URI.create(urlUpSub.toString() + "100&id=200"))
                .version(HttpClient.Version.HTTP_1_1)
                .build(), handler);

        HttpResponse<String> responseUpdateEpic = client.send(HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .uri(URI.create(urlUpEpic.toString() + "100"))
                .version(HttpClient.Version.HTTP_1_1)
                .build(), handler);

        assertEquals("Задача с идентификатором 0 не найдена.", responseUpdateTask.body());
        assertEquals("Задача с идентификатором 100 не найдена.", responseUpdateEpic.body());
        assertEquals("Задача с идентификатором 200 не найдена.", responseUpdateSub.body());
    }

    @Test
    public void deleteTaskEmptyTest() throws IOException, InterruptedException {
        HttpResponse<String> responseTaskDelete = client.send(HttpRequest.newBuilder()
                .DELETE()
                .uri(urlTask)
                .version(HttpClient.Version.HTTP_1_1)
                .build(), handler);
        HttpResponse<String> responseEpicDelete = client.send(HttpRequest.newBuilder()
                .DELETE()
                .uri(urlEpic)
                .version(HttpClient.Version.HTTP_1_1)
                .build(), handler);
        HttpResponse<String> responseSubDelete = client.send(HttpRequest.newBuilder()
                .DELETE()
                .uri(urlSub)
                .version(HttpClient.Version.HTTP_1_1)
                .build(), handler);

        HttpResponse<String> responseTaskDeleteDouble = client.send(HttpRequest.newBuilder()
                .DELETE()
                .uri(urlTask)
                .version(HttpClient.Version.HTTP_1_1)
                .build(), handler);
        HttpResponse<String> responseEpicDeleteDouble = client.send(HttpRequest.newBuilder()
                .DELETE()
                .uri(urlEpic)
                .version(HttpClient.Version.HTTP_1_1)
                .build(), handler);
        HttpResponse<String> responseSubDeleteDouble = client.send(HttpRequest.newBuilder()
                .DELETE()
                .uri(urlSub)
                .version(HttpClient.Version.HTTP_1_1)
                .build(), handler);

        HttpResponse<String> responseTaskGet = client.send(HttpRequest.newBuilder()
                .GET()
                .uri(urlTask)
                .version(HttpClient.Version.HTTP_1_1)
                .build(), handler);

        HttpResponse<String> responseEpicGet = client.send(HttpRequest.newBuilder()
                .GET()
                .uri(urlEpic)
                .version(HttpClient.Version.HTTP_1_1)
                .build(), handler);

        HttpResponse<String> responseSubGet = client.send(HttpRequest.newBuilder()
                .GET()
                .uri(urlSub)
                .version(HttpClient.Version.HTTP_1_1)
                .build(), handler);

        assertEquals("Задачи удалены.", responseTaskDeleteDouble.body());
        assertEquals("[]", responseTaskGet.body());

        assertEquals("Задачи удалены.", responseEpicDeleteDouble.body());
        assertEquals("[]", responseEpicGet.body());

        assertEquals("Задачи удалены.", responseSubDeleteDouble.body());
        assertEquals("[]", responseSubGet.body());
    }

    @Test
    public void deleteTaskIdEmptyTest() throws IOException, InterruptedException {
        HttpResponse<String> responseTaskDelete = client.send(HttpRequest.newBuilder()
                .DELETE()
                .uri(urlTask)
                .version(HttpClient.Version.HTTP_1_1)
                .build(), handler);
        HttpResponse<String> responseEpicDelete = client.send(HttpRequest.newBuilder()
                .DELETE()
                .uri(urlEpic)
                .version(HttpClient.Version.HTTP_1_1)
                .build(), handler);
        HttpResponse<String> responseSubDelete = client.send(HttpRequest.newBuilder()
                .DELETE()
                .uri(urlSub)
                .version(HttpClient.Version.HTTP_1_1)
                .build(), handler);

        HttpResponse<String> responseTaskDeleteId = client.send(HttpRequest.newBuilder()
                .DELETE()
                .uri(URI.create(urlTaskId.toString() + "0"))
                .version(HttpClient.Version.HTTP_1_1)
                .build(), handler);

        HttpResponse<String> responseTaskGetId = client.send(HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(urlTaskId.toString() + "0"))
                .version(HttpClient.Version.HTTP_1_1)
                .build(), handler);

        HttpResponse<String> responseEpicDeleteId = client.send(HttpRequest.newBuilder()
                .DELETE()
                .uri(URI.create(urlEpicId.toString() + "100"))
                .version(HttpClient.Version.HTTP_1_1)
                .build(), handler);

        HttpResponse<String> responseEpicGetId = client.send(HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(urlEpicId.toString() + "100"))
                .version(HttpClient.Version.HTTP_1_1)
                .build(), handler);

        HttpResponse<String> responseSubDeleteId = client.send(HttpRequest.newBuilder()
                .DELETE()
                .uri(URI.create(urlSubId.toString() + "200"))
                .version(HttpClient.Version.HTTP_1_1)
                .build(), handler);

        HttpResponse<String> responseSubGetId = client.send(HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(urlSubId.toString() + "200"))
                .version(HttpClient.Version.HTTP_1_1)
                .build(), handler);

        assertEquals("Задача с идентификатором 0 не найдена", responseTaskGetId.body());
        assertEquals("Задача с идентификатором 100 не найдена", responseEpicGetId.body());
        assertEquals("Задача с идентификатором 200 не найдена", responseSubGetId.body());

        assertEquals("Задача удалена.", responseTaskDeleteId.body());
        assertEquals("Задача удалена.", responseEpicDeleteId.body());
        assertEquals("Задача удалена.", responseSubDeleteId.body());
    }

    //С неверным идентификатором задачи (пустой и/или несуществующий идентификатор).
    @Test
    public void getIdTaskErrorTest() throws IOException, InterruptedException {
        HttpResponse<String> responseTaskGetId = client.send(HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(urlTaskId.toString() + "11"))
                .version(HttpClient.Version.HTTP_1_1)
                .build(), handler);

        HttpResponse<String> responseEpicGetId = client.send(HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(urlEpicId.toString() + "101"))
                .version(HttpClient.Version.HTTP_1_1)
                .build(), handler);

        HttpResponse<String> responseSubGetId = client.send(HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(urlSubId.toString() + "201"))
                .version(HttpClient.Version.HTTP_1_1)
                .build(), handler);

        assertEquals("Задача с идентификатором 11 не найдена", responseTaskGetId.body());
        assertEquals("Задача с идентификатором 101 не найдена", responseEpicGetId.body());
        assertEquals("Задача с идентификатором 201 не найдена", responseSubGetId.body());
    }

    @Test
    public void getAllSubEpicErrorTest() throws IOException, InterruptedException {
        HttpResponse<String> responseAllSubGet = client.send(HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(urlAllsub.toString() + "101"))
                .version(HttpClient.Version.HTTP_1_1)
                .build(), handler);

        assertEquals("[]", responseAllSubGet.body());
    }

    @Test
    public void getHistoryErrorTest() throws IOException, InterruptedException {
        HttpResponse<String> responseTaskGetId = client.send(HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(urlTaskId.toString() + "11"))
                .version(HttpClient.Version.HTTP_1_1)
                .build(), handler);

        HttpResponse<String> responseEpicGetId = client.send(HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(urlEpicId.toString() + "101"))
                .version(HttpClient.Version.HTTP_1_1)
                .build(), handler);

        HttpResponse<String> responseSubGetId = client.send(HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(urlSubId.toString() + "201"))
                .version(HttpClient.Version.HTTP_1_1)
                .build(), handler);

        HttpResponse<String> responseHistoryGet = client.send(HttpRequest.newBuilder()
                .GET()
                .uri(urlHistory)
                .version(HttpClient.Version.HTTP_1_1)
                .build(), handler);

        assertEquals("Задача с идентификатором 11 не найдена", responseTaskGetId.body());
        assertEquals("Задача с идентификатором 101 не найдена", responseEpicGetId.body());
        assertEquals("Задача с идентификатором 201 не найдена", responseSubGetId.body());
        assertEquals("[]", responseHistoryGet.body());
    }

    @Test
    public void updateTaskErrorTest() throws IOException, InterruptedException {
        managerTask.updateTask(task.getId(), "e", "e", "DONE");
        managerTask.updateEpic(epic.getId(), "e", "e");
        managerTask.updateSubtask(epic.getId(), sub.getId(), "e", "e", "DONE");

        HttpResponse<String> responseUpdateTask = client.send(HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                .uri(URI.create(urlUpTask.toString() + "11"))
                .version(HttpClient.Version.HTTP_1_1)
                .build(), handler);

        HttpResponse<String> responseUpdateSub = client.send(HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(sub)))
                .uri(URI.create(urlUpSub.toString() + "101&id=201"))
                .version(HttpClient.Version.HTTP_1_1)
                .build(), handler);

        HttpResponse<String> responseUpdateEpic = client.send(HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .uri(URI.create(urlUpEpic.toString() + "101"))
                .version(HttpClient.Version.HTTP_1_1)
                .build(), handler);

        HttpResponse<String> responseTaskGetId = client.send(HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(urlTaskId.toString() + "11"))
                .version(HttpClient.Version.HTTP_1_1)
                .build(), handler);

        HttpResponse<String> responseEpicGetId = client.send(HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(urlEpicId.toString() + "101"))
                .version(HttpClient.Version.HTTP_1_1)
                .build(), handler);

        HttpResponse<String> responseSubGetId = client.send(HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(urlSubId.toString() + "201"))
                .version(HttpClient.Version.HTTP_1_1)
                .build(), handler);

        assertEquals("Задача с идентификатором 11 не найдена.", responseUpdateTask.body());
        assertEquals("Задача с идентификатором 101 не найдена.", responseUpdateEpic.body());
        assertEquals("Задача с идентификатором 201 не найдена.", responseUpdateSub.body());
    }

    @Test
    public void deleteTaskIdErrorTest() throws IOException, InterruptedException {
        managerTask.removeTaskId(task.getId());
        managerTask.removeEpicId(epic.getId());
        managerTask.removeSubtaskId(sub.getId());

        HttpResponse<String> responseTaskDelete = client.send(HttpRequest.newBuilder()
                .DELETE()
                .uri(URI.create(urlTaskId.toString() + "11"))
                .version(HttpClient.Version.HTTP_1_1)
                .build(), handler);

        HttpResponse<String> responseTaskGetId = client.send(HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(urlTaskId.toString() + "11"))
                .version(HttpClient.Version.HTTP_1_1)
                .build(), handler);

        HttpResponse<String> responseEpicDelete = client.send(HttpRequest.newBuilder()
                .DELETE()
                .uri(URI.create(urlEpicId.toString() + "101"))
                .version(HttpClient.Version.HTTP_1_1)
                .build(), handler);

        HttpResponse<String> responseEpicGetId = client.send(HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(urlEpicId.toString() + "101"))
                .version(HttpClient.Version.HTTP_1_1)
                .build(), handler);

        HttpResponse<String> responseSubDelete = client.send(HttpRequest.newBuilder()
                .DELETE()
                .uri(URI.create(urlSubId.toString() + "201"))
                .version(HttpClient.Version.HTTP_1_1)
                .build(), handler);

        HttpResponse<String> responseSubGetId = client.send(HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(urlSubId.toString() + "201"))
                .version(HttpClient.Version.HTTP_1_1)
                .build(), handler);

        assertEquals("Задача с идентификатором 11 не найдена", responseTaskGetId.body());
        assertEquals("Задача с идентификатором 101 не найдена", responseEpicGetId.body());
        assertEquals("Задача с идентификатором 201 не найдена", responseSubGetId.body());

        assertEquals("Задача удалена.", responseTaskDelete.body());
        assertEquals("Задача удалена.", responseEpicDelete.body());
        assertEquals("Задача удалена.", responseSubDelete.body());
    }
}