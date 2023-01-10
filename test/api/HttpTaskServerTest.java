package api;

import com.google.gson.Gson;

import manager.InMemoryTaskManager;
import manager.TaskManager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;


import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;

import java.net.http.HttpResponse;
import java.time.Duration;



class HttpTaskServerTest {

    Gson gson = new Gson();
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
        urlTaskId = URI.create("http://localhost:8080/tasks/task/?id=");
        urlEpicId = URI.create("http://localhost:8080/tasks/epic/?id=");
        urlSubId = URI.create("http://localhost:8080/tasks/subtask/?id=");
        urlAllsub = URI.create("http://localhost:8080/tasks/subtask/epic/?id=");
        urlHistory = URI.create("http://localhost:8080/tasks/history");
        urlUpTask = URI.create("http://localhost:8080/tasks/uptask/?id=");
        urlUpEpic = URI.create("http://localhost:8080/tasks/upepic/?id=");
        urlUpSub = URI.create("http://localhost:8080/tasks/upsubtask/?id=");
    }

    @AfterEach
    public void stopServer() throws IOException {
        KVServer.stop();
    }

    //Со стандартным поведением.
//    @Test
//    public void createAndGetTaskTest() throws IOException, InterruptedException {
//        Task task = managerTask.createTask("a", "a", "DONE");
//        Epic epic = managerTask.createEpic("b", "b", "DONE");
//        Subtask sub = managerTask.createSubtask(epic.getId(), "c", "c", "DONE");
//
//        HttpResponse<String> responseTaskPost = client.send(HttpRequest.newBuilder()
//                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
//                .uri(urlTask)
//                .version(HttpClient.Version.HTTP_1_1)
//                .build(), handler);
//
//        HttpResponse<String> responseTaskGet = client.send(HttpRequest.newBuilder()
//                .GET()
//                .uri(urlTask)
//                .version(HttpClient.Version.HTTP_1_1)
//                .build(), handler);
//
////        HttpResponse<String> responseEpic = client.send(HttpRequest.newBuilder()
////                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
////                .uri(urlTask)
////                .version(HttpClient.Version.HTTP_1_1)
////                .build(), handler);
////        HttpResponse<String> responseSub = client.send(HttpRequest.newBuilder()
////                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
////                .uri(urlTask)
////                .version(HttpClient.Version.HTTP_1_1)
////                .build(), handler);
//
//        System.out.println(managerTask.getTask());
//        assertEquals(gson.toJson(managerTask.getTask()), responseTaskGet.body());
//    }

//    private void save(HttpExchange httpExchange) throws IOException {
//        try {
//            manager.saveKV();
//            String response = "Данные сохранены!";
//            httpExchange.sendResponseHeaders(404, 0);
//            try (OutputStream os = httpExchange.getResponseBody()) {
//                os.write(response.getBytes());
//            }
//        } finally {
//            httpExchange.close();
//        }
//    }
//
//    private void getPrioritizedTasks(HttpExchange httpExchange) throws IOException {
//        List<Task> task = manager.getPrioritizedTasks();
//        String response = gson.toJson(task);
//        httpExchange.sendResponseHeaders(200, 0);
//        try (OutputStream os = httpExchange.getResponseBody()) {
//            os.write(response.getBytes());
//        }
//        httpExchange.close();
//    }
//
//    private void getTaskHandler(HttpExchange httpExchange) throws IOException {
//        String response = gson.toJson(manager.getTask());
//        httpExchange.sendResponseHeaders(200, 0);
//        try (OutputStream os = httpExchange.getResponseBody()) {
//            os.write(response.getBytes());
//        }
//        httpExchange.close();
//    }
//
//    private void getEpicHandler(HttpExchange httpExchange) throws IOException {
//        String response = gson.toJson(manager.getEpic());
//        httpExchange.sendResponseHeaders(200, 0);
//        try (OutputStream os = httpExchange.getResponseBody()) {
//            os.write(response.getBytes());
//        }
//        httpExchange.close();
//    }
//
//    private void getSubHandler(HttpExchange httpExchange) throws IOException {
//        String response = gson.toJson(manager.getSubtask());
//        httpExchange.sendResponseHeaders(200, 0);
//        try (OutputStream os = httpExchange.getResponseBody()) {
//            os.write(response.getBytes());
//        }
//        httpExchange.close();
//    }
//
//    private void getTaskId(HttpExchange httpExchange) throws IOException {
//        Optional<Integer> idOpt = getTasksId(httpExchange);
//        if (idOpt.isEmpty()) {
//            writeResponse(httpExchange, "Некорректный идентификатор задачи", 400);
//            return;
//        }
//
//        Task task = manager.getTaskId(idOpt.get());
//        if (task != null) {
//            String response = gson.toJson(task);
//            httpExchange.sendResponseHeaders(200, 0);
//            try (OutputStream os = httpExchange.getResponseBody()) {
//                String id = "Задача #" + idOpt.get() + ":\n";
//                os.write(id.getBytes());
//                os.write(response.getBytes());
//            }
//        } else {
//            writeResponse(httpExchange, "Задача с идентификатором " + idOpt.get() + " не найдена", 404);
//        }
//        httpExchange.close();
//    }
//
//    private void getEpicId(HttpExchange httpExchange) throws IOException {
//        Optional<Integer> idOpt = getTasksId(httpExchange);
//        if (idOpt.isEmpty()) {
//            writeResponse(httpExchange, "Некорректный идентификатор задачи", 400);
//            return;
//        }
//
//        Task task = manager.getEpicId(idOpt.get());
//        if (task != null) {
//            String response = gson.toJson(task);
//            httpExchange.sendResponseHeaders(200, 0);
//            try (OutputStream os = httpExchange.getResponseBody()) {
//                String id = "Задача #" + idOpt.get() + ":\n";
//                os.write(id.getBytes());
//                os.write(response.getBytes());
//            }
//        } else {
//            writeResponse(httpExchange, "Задача с идентификатором " + idOpt.get() + " не найдена", 404);
//        }
//        httpExchange.close();
//    }
//
//    private void getSubId(HttpExchange httpExchange) throws IOException {
//        Optional<Integer> idOpt = getTasksId(httpExchange);
//        if (idOpt.isEmpty()) {
//            writeResponse(httpExchange, "Некорректный идентификатор задачи", 400);
//            return;
//        }
//
//        Task task = manager.getSubtaskId(idOpt.get());
//        if (task != null) {
//            String response = gson.toJson(task);
//            httpExchange.sendResponseHeaders(200, 0);
//            try (OutputStream os = httpExchange.getResponseBody()) {
//                String id = "Задача #" + idOpt.get() + ":\n";
//                os.write(id.getBytes());
//                os.write(response.getBytes());
//            }
//        } else {
//            writeResponse(httpExchange, "Задача с идентификатором " + idOpt.get() + " не найдена", 404);
//        }
//        httpExchange.close();
//    }
//
//    private void getAllSubHandler(HttpExchange httpExchange) throws IOException {
//        Optional<Integer> idOpt = getTasksId(httpExchange);
//        if (idOpt.isEmpty()) {
//            writeResponse(httpExchange, "Некорректный идентификатор задачи", 400);
//            return;
//        }
//
//        List<Subtask> task = manager.getListAllSubtask(idOpt.get());
//        if (task != null) {
//            String response = gson.toJson(task);
//            httpExchange.sendResponseHeaders(200, 0);
//            try (OutputStream os = httpExchange.getResponseBody()) {
//                os.write(response.getBytes());
//            }
//        } else {
//            writeResponse(httpExchange, "Подзадачи epic с идентификатором " + idOpt.get() + " не найдена", 404);
//        }
//        httpExchange.close();
//    }
//
//    private void getHistoryHandler(HttpExchange httpExchange) throws IOException {
//        String response = gson.toJson(manager.getHistory());
//        httpExchange.sendResponseHeaders(200, 0);
//        try (OutputStream os = httpExchange.getResponseBody()) {
//            os.write(response.getBytes());
//        }
//        httpExchange.close();
//    }
//
//    private void createTaskHandler(HttpExchange httpExchange) throws IOException {
//        String[] newTask = getInfoTask(httpExchange);
//        if (!newTask[0].equals("null") && !newTask[1].equals("null") && !newTask[2].equals("null")) {
//            httpExchange.sendResponseHeaders(200, 0);
//            try {
//                String response = gson.toJson(manager.createTask(newTask[0], newTask[1], newTask[2]));
//                try (OutputStream os = httpExchange.getResponseBody()) {
//                    os.write("Задача создана:\n".getBytes());
//                    os.write(response.getBytes());
//                }
//            } catch (InMemoryTaskManager.ValidationTaskException e) {
//                try (OutputStream os = httpExchange.getResponseBody()) {
//                    os.write(e.getMessage().getBytes());
//                }
//            }
//        } else {
//            writeResponse(httpExchange, "Поля задачи не могут быть пустыми.", 400);
//        }
//        httpExchange.close();
//    }
//
//    private void createEpicHandler(HttpExchange httpExchange) throws IOException {
//        String[] newTask = getInfoTask(httpExchange);
//        if (!newTask[0].equals("null") && !newTask[1].equals("null") && !newTask[2].equals("null")) {
//            httpExchange.sendResponseHeaders(200, 0);
//            try {
//                String response = gson.toJson(manager.createEpic(newTask[0], newTask[1], newTask[2]));
//                try (OutputStream os = httpExchange.getResponseBody()) {
//                    os.write("Задача создана:\n".getBytes());
//                    os.write(response.getBytes());
//                }
//            } catch (InMemoryTaskManager.ValidationTaskException e) {
//                try (OutputStream os = httpExchange.getResponseBody()) {
//                    os.write(e.getMessage().getBytes());
//                }
//            }
//        } else {
//            writeResponse(httpExchange, "Поля задачи не могут быть пустыми.", 400);
//        }
//        httpExchange.close();
//    }
//
//    private void createSubHandler(HttpExchange httpExchange) throws IOException {
//        Optional<Integer> idOpt = getTasksId(httpExchange);
//        if (idOpt.isEmpty()) {
//            writeResponse(httpExchange, "Некорректный идентификатор задачи", 400);
//            return;
//        }
//        String[] newTask = getInfoTask(httpExchange);
//        if (!newTask[0].equals("null") && !newTask[1].equals("null") && !newTask[2].equals("null")) {
//            httpExchange.sendResponseHeaders(200, 0);
//            try {
//                String response = gson.toJson(manager.createSubtask(idOpt.get(), newTask[0], newTask[1], newTask[2]));
//                try (OutputStream os = httpExchange.getResponseBody()) {
//                    os.write("Подзадача создана:\n".getBytes());
//                    os.write(response.getBytes());
//                }
//            } catch (InMemoryTaskManager.ValidationTaskException e) {
//                try (OutputStream os = httpExchange.getResponseBody()) {
//                    os.write(e.getMessage().getBytes());
//                }
//            }
//        } else {
//            writeResponse(httpExchange, "Поля задачи не могут быть пустыми.", 400);
//        }
//        httpExchange.close();
//    }
//
//
//    private void updateTaskHandler(HttpExchange httpExchange) throws IOException {
//        Optional<Integer> idOpt = getTasksId(httpExchange);
//        if (idOpt.isEmpty()) {
//            writeResponse(httpExchange, "Некорректный идентификатор задачи", 400);
//            return;
//        }
//        String[] newTask = getInfoTask(httpExchange);
//        if (!newTask[0].equals("null") && !newTask[1].equals("null") && !newTask[2].equals("null")) {
//            httpExchange.sendResponseHeaders(200, 0);
//            manager.updateTask(idOpt.get(), newTask[0], newTask[1], newTask[2]);
//            String response = gson.toJson(manager.getTaskId(idOpt.get()));
//            try (OutputStream os = httpExchange.getResponseBody()) {
//                os.write("Задача обновлена:\n".getBytes());
//                os.write(response.getBytes());
//            }
//        } else {
//            writeResponse(httpExchange, "Поля задачи не могут быть пустыми.", 400);
//        }
//        httpExchange.close();
//    }
//
//    private void updateEpicHandler(HttpExchange httpExchange) throws IOException {
//        Optional<Integer> idOpt = getTasksId(httpExchange);
//        if (idOpt.isEmpty()) {
//            writeResponse(httpExchange, "Некорректный идентификатор задачи", 400);
//            return;
//        }
//        String[] newTask = getInfoTask(httpExchange);
//        if (!newTask[0].equals("null") && !newTask[1].equals("null")) {
//            httpExchange.sendResponseHeaders(200, 0);
//            manager.updateEpic(idOpt.get(), newTask[0], newTask[1]);
//            String response = gson.toJson(manager.getEpicId(idOpt.get()));
//            try (OutputStream os = httpExchange.getResponseBody()) {
//                os.write("Задача обновлена:\n".getBytes());
//                os.write(response.getBytes());
//            }
//        } else {
//            writeResponse(httpExchange, "Поля задачи не могут быть пустыми.", 400);
//        }
//        httpExchange.close();
//    }
//
//    private void updateSubHandler(HttpExchange httpExchange) throws IOException {
//        Optional<Integer> idOpt = getTasksId(httpExchange);
//        if (idOpt.isEmpty()) {
//            writeResponse(httpExchange, "Некорректный идентификатор задачи", 400);
//            return;
//        }
//        String[] newId = idOpt.toString().split("");
//        String[] newTask = getInfoTask(httpExchange);
//        Integer idEpic = Integer.parseInt(newId[0]);
//        Integer idSub = Integer.parseInt(newId[1]);
//        if (!newTask[0].equals("null") && !newTask[1].equals("null") && !newTask[2].equals("null")) {
//            httpExchange.sendResponseHeaders(200, 0);
//            manager.updateSubtask(idEpic, idSub, newTask[0], newTask[1], newTask[2]);
//            String response = gson.toJson(manager.getTaskId(idOpt.get()));
//            try (OutputStream os = httpExchange.getResponseBody()) {
//                os.write("Задача обновлена:\n".getBytes());
//                os.write(response.getBytes());
//            }
//        } else {
//            writeResponse(httpExchange, "Поля задачи не могут быть пустыми.", 400);
//        }
//        httpExchange.close();
//    }
//
//    private void removeTaskHandler(HttpExchange httpExchange) throws IOException {
//        manager.removeTask();
//        if (manager.getTask().isEmpty()) {
//            httpExchange.sendResponseHeaders(200, 0);
//            try (OutputStream os = httpExchange.getResponseBody()) {
//                os.write("Задачи удалены.".getBytes());
//            }
//        } else {
//            writeResponse(httpExchange, "Ошибка удаления.", 400);
//        }
//        httpExchange.close();
//    }
//
//    private void removeEpicHandler(HttpExchange httpExchange) throws IOException {
//        manager.removeEpic();
//        if (manager.getEpic().isEmpty()) {
//            httpExchange.sendResponseHeaders(200, 0);
//            try (OutputStream os = httpExchange.getResponseBody()) {
//                os.write("Задачи удалены.".getBytes());
//            }
//        } else {
//            writeResponse(httpExchange, "Ошибка удаления.", 400);
//        }
//        httpExchange.close();
//    }
//
//
//    private void removeSubHandler(HttpExchange httpExchange) throws IOException {
//        manager.removeSubtask();
//        if (manager.getSubtask().isEmpty()) {
//            httpExchange.sendResponseHeaders(200, 0);
//            try (OutputStream os = httpExchange.getResponseBody()) {
//                os.write("Задачи удалены.".getBytes());
//            }
//        } else {
//            writeResponse(httpExchange, "Ошибка удаления.", 400);
//        }
//        httpExchange.close();
//    }
//
//    private void removeTaskIdHandler(HttpExchange httpExchange) throws IOException {
//        Optional<Integer> idOpt = getTasksId(httpExchange);
//        if (idOpt.isEmpty()) {
//            writeResponse(httpExchange, "Некорректный идентификатор задачи", 400);
//            return;
//        }
//
//        manager.removeTaskId(idOpt.get());
//        for (Task task : manager.getTask()) {
//            if (task.getId() == idOpt.get()) {
//                writeResponse(httpExchange, "Ошибка удаления.", 400);
//                return;
//            }
//        }
//        httpExchange.sendResponseHeaders(200, 0);
//        httpExchange.close();
//    }
//
//    private void removeEpicIdHandler(HttpExchange httpExchange) throws IOException {
//        Optional<Integer> idOpt = getTasksId(httpExchange);
//        if (idOpt.isEmpty()) {
//            writeResponse(httpExchange, "Некорректный идентификатор задачи", 400);
//            return;
//        }
//
//        manager.removeEpicId(idOpt.get());
//        for (Task task : manager.getEpic()) {
//            if (task.getId() == idOpt.get()) {
//                writeResponse(httpExchange, "Ошибка удаления.", 400);
//                return;
//            }
//        }
//        httpExchange.sendResponseHeaders(200, 0);
//        httpExchange.close();
//    }
//
//    private void removeSubIdHandler(HttpExchange httpExchange) throws IOException {
//        Optional<Integer> idOpt = getTasksId(httpExchange);
//        if (idOpt.isEmpty()) {
//            writeResponse(httpExchange, "Некорректный идентификатор задачи", 400);
//            return;
//        }
//
//        manager.removeSubtaskId(idOpt.get());
//        for (Task task : manager.getSubtask()) {
//            if (task.getId() == idOpt.get()) {
//                writeResponse(httpExchange, "Ошибка удаления.", 400);
//                return;
//            }
//        }
//        httpExchange.sendResponseHeaders(200, 0);
//        httpExchange.close();
//    }

}