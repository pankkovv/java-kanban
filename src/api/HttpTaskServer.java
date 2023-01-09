package api;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import manager.FileBackedTasksManager;
import manager.InMemoryTaskManager;
import manager.Managers;
import manager.TaskManager;
import model.Endpoint;
import model.Epic;
import model.Subtask;
import model.Task;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

import static jdk.internal.util.xml.XMLStreamWriter.DEFAULT_CHARSET;

public class HttpTaskServer {
    private static final int PORT = 8080;

    static Gson gson = new Gson();
    public HttpServer server;
    static TaskManager manager = Managers.getDefault();

    public HttpTaskServer() throws IOException {
        server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/tasks", new MethodHandler());
        server.createContext("/tasks/task", new MethodHandler());
        server.createContext("/tasks/epic", new MethodHandler());
        server.createContext("/tasks/subtask", new MethodHandler());
        server.createContext("/tasks/task/?id=", new MethodHandler());
        server.createContext("/tasks/epic/?id=", new MethodHandler());
        server.createContext("/tasks/subtask/?id=", new MethodHandler());
        server.createContext("/tasks/subtask/epic/?id=", new MethodHandler());
        server.createContext("/tasks/history", new MethodHandler());
        server.createContext("/tasks/uptask/?id=", new MethodHandler());
        server.createContext("/tasks/upepic/?id=", new MethodHandler());
        server.createContext("/tasks/upsubtask/?id=", new MethodHandler());
    }

     static class MethodHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

            switch (endpoint) {
                case GET_TASKS:
                    getPrioritizedTasks(exchange);
                    break;

                case GET_TASK:
                    getTaskHandler(exchange);
                    break;

                case GET_EPIC:
                    getEpicHandler(exchange);
                    break;

                case GET_SUB:
                    getSubHandler(exchange);
                    break;

                case GET_TASKID:
                    getTaskId(exchange);
                    break;

                case GET_EPICID:
                    getEpicId(exchange);
                    break;

                case GET_SUBID:
                    getSubId(exchange);
                    break;

                case GET_ALLSUBEPIC:
                    getAllSubHandler(exchange);
                    break;

                case GET_HISTORY:
                    getHistoryHandler(exchange);
                    break;

                case POST_TASK:
                    createTaskHandler(exchange);
                    break;

                case POST_EPIC:
                    createEpicHandler(exchange);
                    break;

                case POST_SUB:
                    createSubHandler(exchange);
                    break;

                case POST_UPDATETASK:
                    updateTaskHandler(exchange);
                    break;

                case POST_UPDATEEPIC:
                    updateEpicHandler(exchange);
                    break;

                case POST_UPDATESUB:
                    updateSubHandler(exchange);
                    break;

                case DELETE_TASK:
                    removeTaskHandler(exchange);
                    break;

                case DELETE_EPIC:
                    removeEpicHandler(exchange);
                    break;

                case DELETE_SUB:
                    removeSubHandler(exchange);
                    break;

                case DELETE_TASKID:
                    removeTaskIdHandler(exchange);
                    break;

                case DELETE_EPICID:
                    removeEpicIdHandler(exchange);
                    break;

                case DELETE_SUBID:
                    removeSubIdHandler(exchange);
                    break;

                case UNKNOW:
                    writeResponse(exchange, "Некорректное действие.", 400);
            }
        }
    }

    private static Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] pathParts = requestPath.split("/");

        switch (requestMethod) {
            case "GET":
                if (pathParts.length == 2 && pathParts[1].equals("tasks")) {
                    return Endpoint.GET_TASKS;
                } else if (pathParts.length == 3 && pathParts[1].equals("tasks")) {
                    if (pathParts[2].equals("task")) {
                        return Endpoint.GET_TASK;
                    }
                    if (pathParts[2].equals("epic")) {
                        return Endpoint.GET_EPIC;
                    }
                    if (pathParts[2].equals("subtask")) {
                        return Endpoint.GET_SUB;
                    }
                    if (pathParts[2].equals("history")) {
                        return Endpoint.GET_HISTORY;
                    }
                } else if (pathParts.length == 4 && pathParts[1].equals("tasks") && pathParts[3].startsWith("?id")) {
                    if (pathParts[2].equals("task")) {
                        return Endpoint.GET_TASKID;
                    }
                    if (pathParts[2].equals("epic")) {
                        return Endpoint.GET_EPICID;
                    }
                    if (pathParts[2].equals("subtask")) {
                        return Endpoint.GET_SUBID;
                    }
                }
                break;

            case "POST":
                if (pathParts.length == 3 && pathParts[1].equals("tasks")) {
                    if (pathParts[2].equals("task")) {
                        return Endpoint.POST_TASK;
                    }
                    if (pathParts[2].equals("epic")) {
                        return Endpoint.POST_EPIC;
                    }
                }
                if (pathParts.length == 4 && pathParts[1].equals("tasks") && pathParts[3].startsWith("?id")) {
                    if (pathParts[2].equals("subtask")) {
                        return Endpoint.POST_SUB;
                    }
                    if (pathParts[2].equals("uptask")) {
                        return Endpoint.POST_UPDATETASK;
                    }
                    if (pathParts[2].equals("upepic")) {
                        return Endpoint.POST_UPDATEEPIC;
                    }
                    if (pathParts[2].equals("upsubtask")) {
                        return Endpoint.POST_UPDATESUB;
                    }
                }
                break;

            case "DELETE":
                if (pathParts.length == 3 && pathParts[1].equals("tasks")) {
                    if (pathParts[2].equals("task")) {
                        return Endpoint.DELETE_TASK;
                    }
                    if (pathParts[2].equals("epic")) {
                        return Endpoint.DELETE_EPIC;
                    }
                    if (pathParts[2].equals("subtask")) {
                        return Endpoint.DELETE_SUB;
                    }
                }
                if (pathParts.length == 4 && pathParts[1].equals("tasks")) {
                    if (pathParts[2].equals("task") && pathParts[3].startsWith("?id=")) {
                        return Endpoint.DELETE_TASKID;
                    }
                    if (pathParts[2].equals("epic")) {
                        return Endpoint.DELETE_EPICID;
                    }
                    if (pathParts[2].equals("subtask")) {
                        return Endpoint.DELETE_SUBID;
                    }
                }
                break;
        }
        return Endpoint.UNKNOW;
    }

    private static void writeResponse(HttpExchange exchange, String responseString, int responseCode) throws IOException {
        if (responseString.isBlank()) {
            exchange.sendResponseHeaders(responseCode, 0);
        } else {
            byte[] bytes = responseString.getBytes(DEFAULT_CHARSET);
            exchange.sendResponseHeaders(responseCode, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        }
        exchange.close();
    }

    private static Optional<Integer> getTasksId(HttpExchange exchange) {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        String[] parseParts = pathParts[3].split("&");
        if (parseParts.length == 1) {
            try {
                String[] parseId = parseParts[0].split("=");
                return Optional.of(Integer.parseInt(parseId[1]));
            } catch (NumberFormatException exception) {
                return Optional.empty();
            }
        } else {
            try {
                String[] parseIdEpic = parseParts[0].split("=");
                String[] parseId = parseParts[1].split("=");
                String doubleId = parseIdEpic[1] + parseId[1];
                return Optional.of(Integer.parseInt(doubleId));
            } catch (NumberFormatException exception) {
                return Optional.empty();
            }
        }
    }

    private static String[] getInfoTask(HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
        Task task = gson.fromJson(body, Task.class);
        String[] newTask = new String[]{task.getTitle(), task.getDescription(), task.getStatus()};
        return newTask;
    }

    private static void getPrioritizedTasks(HttpExchange httpExchange) throws IOException {
        List<Task> task = manager.getPrioritizedTasks();
        String response = gson.toJson(task);
        httpExchange.sendResponseHeaders(200, 0);
        try (OutputStream os = httpExchange.getResponseBody()) {
            os.write(response.getBytes());
        }
        httpExchange.close();
    }

    private static void getTaskHandler(HttpExchange httpExchange) throws IOException {
        String response = gson.toJson(manager.getTask());
        httpExchange.sendResponseHeaders(200, 0);
        try (OutputStream os = httpExchange.getResponseBody()) {
            os.write(response.getBytes());
        }
        httpExchange.close();
    }

    private static void getEpicHandler(HttpExchange httpExchange) throws IOException {
        String response = gson.toJson(manager.getEpic());
        httpExchange.sendResponseHeaders(200, 0);
        try (OutputStream os = httpExchange.getResponseBody()) {
            os.write(response.getBytes());
        }
        httpExchange.close();
    }

    private static void getSubHandler(HttpExchange httpExchange) throws IOException {
        String response = gson.toJson(manager.getSubtask());
        httpExchange.sendResponseHeaders(200, 0);
        try (OutputStream os = httpExchange.getResponseBody()) {
            os.write(response.getBytes());
        }
        httpExchange.close();
    }

    private static void getTaskId(HttpExchange httpExchange) throws IOException {
        Optional<Integer> idOpt = getTasksId(httpExchange);
        if (idOpt.isEmpty()) {
            writeResponse(httpExchange, "Некорректный идентификатор задачи", 400);
            return;
        }

        Task task = manager.getTaskId(idOpt.get());
        if (task != null) {
            String response = gson.toJson(task);
            httpExchange.sendResponseHeaders(200, 0);
            try (OutputStream os = httpExchange.getResponseBody()) {
                String id = "Задача #" + idOpt.get() + ":\n";
                os.write(id.getBytes());
                os.write(response.getBytes());
            }
        } else {
            writeResponse(httpExchange, "Задача с идентификатором " + idOpt.get() + " не найдена", 404);
        }
        httpExchange.close();
    }

    private static void getEpicId(HttpExchange httpExchange) throws IOException {
        Optional<Integer> idOpt = getTasksId(httpExchange);
        if (idOpt.isEmpty()) {
            writeResponse(httpExchange, "Некорректный идентификатор задачи", 400);
            return;
        }

        Task task = manager.getEpicId(idOpt.get());
        if (task != null) {
            String response = gson.toJson(task);
            httpExchange.sendResponseHeaders(200, 0);
            try (OutputStream os = httpExchange.getResponseBody()) {
                String id = "Задача #" + idOpt.get() + ":\n";
                os.write(id.getBytes());
                os.write(response.getBytes());
            }
        } else {
            writeResponse(httpExchange, "Задача с идентификатором " + idOpt.get() + " не найдена", 404);
        }
        httpExchange.close();
    }

    private static void getSubId(HttpExchange httpExchange) throws IOException {
        Optional<Integer> idOpt = getTasksId(httpExchange);
        if (idOpt.isEmpty()) {
            writeResponse(httpExchange, "Некорректный идентификатор задачи", 400);
            return;
        }

        Task task = manager.getSubtaskId(idOpt.get());
        if (task != null) {
            String response = gson.toJson(task);
            httpExchange.sendResponseHeaders(200, 0);
            try (OutputStream os = httpExchange.getResponseBody()) {
                String id = "Задача #" + idOpt.get() + ":\n";
                os.write(id.getBytes());
                os.write(response.getBytes());
            }
        } else {
            writeResponse(httpExchange, "Задача с идентификатором " + idOpt.get() + " не найдена", 404);
        }
        httpExchange.close();
    }

    private static void getAllSubHandler(HttpExchange httpExchange) throws IOException {
        Optional<Integer> idOpt = getTasksId(httpExchange);
        if (idOpt.isEmpty()) {
            writeResponse(httpExchange, "Некорректный идентификатор задачи", 400);
            return;
        }

        List<Subtask> task = manager.getListAllSubtask(idOpt.get());
        if (task != null) {
            String response = gson.toJson(task);
            httpExchange.sendResponseHeaders(200, 0);
            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        } else {
            writeResponse(httpExchange, "Подзадачи epic с идентификатором " + idOpt.get() + " не найдена", 404);
        }
        httpExchange.close();
    }

    private static void getHistoryHandler(HttpExchange httpExchange) throws IOException {
        String response = gson.toJson(manager.getHistory());
        httpExchange.sendResponseHeaders(200, 0);
        try (OutputStream os = httpExchange.getResponseBody()) {
            os.write(response.getBytes());
        }
        httpExchange.close();
    }

    private static void createTaskHandler(HttpExchange httpExchange) throws IOException {
        String[] newTask = getInfoTask(httpExchange);
        if (!newTask[0].equals("null") && !newTask[1].equals("null") && !newTask[2].equals("null")) {
            httpExchange.sendResponseHeaders(200, 0);
            try {
                String response = gson.toJson(manager.createTask(newTask[0], newTask[1], newTask[2]));
                try (OutputStream os = httpExchange.getResponseBody()) {
                    os.write("Задача создана:\n".getBytes());
                    os.write(response.getBytes());
                }
            } catch (InMemoryTaskManager.ValidationTaskException e) {
                try (OutputStream os = httpExchange.getResponseBody()) {
                    os.write(e.getMessage().getBytes());
                }
            }
        } else {
            writeResponse(httpExchange, "Поля задачи не могут быть пустыми.", 400);
        }
        httpExchange.close();
    }

    private static void createEpicHandler(HttpExchange httpExchange) throws IOException {
        String[] newTask = getInfoTask(httpExchange);
        if (!newTask[0].equals("null") && !newTask[1].equals("null") && !newTask[2].equals("null")) {
            httpExchange.sendResponseHeaders(200, 0);
            try {
                String response = gson.toJson(manager.createEpic(newTask[0], newTask[1], newTask[2]));
                try (OutputStream os = httpExchange.getResponseBody()) {
                    os.write("Задача создана:\n".getBytes());
                    os.write(response.getBytes());
                }
            } catch (InMemoryTaskManager.ValidationTaskException e) {
                try (OutputStream os = httpExchange.getResponseBody()) {
                    os.write(e.getMessage().getBytes());
                }
            }
        } else {
            writeResponse(httpExchange, "Поля задачи не могут быть пустыми.", 400);
        }
        httpExchange.close();
    }

    private static void createSubHandler(HttpExchange httpExchange) throws IOException {
        Optional<Integer> idOpt = getTasksId(httpExchange);
        if (idOpt.isEmpty()) {
            writeResponse(httpExchange, "Некорректный идентификатор задачи", 400);
            return;
        }
        String[] newTask = getInfoTask(httpExchange);
        if (!newTask[0].equals("null") && !newTask[1].equals("null") && !newTask[2].equals("null")) {
            httpExchange.sendResponseHeaders(200, 0);
            try {
                String response = gson.toJson(manager.createSubtask(idOpt.get(), newTask[0], newTask[1], newTask[2]));
                try (OutputStream os = httpExchange.getResponseBody()) {
                    os.write("Подзадача создана:\n".getBytes());
                    os.write(response.getBytes());
                }
            } catch (InMemoryTaskManager.ValidationTaskException e) {
                try (OutputStream os = httpExchange.getResponseBody()) {
                    os.write(e.getMessage().getBytes());
                }
            }
        } else {
            writeResponse(httpExchange, "Поля задачи не могут быть пустыми.", 400);
        }
        httpExchange.close();
    }


    private static void updateTaskHandler(HttpExchange httpExchange) throws IOException {
        Optional<Integer> idOpt = getTasksId(httpExchange);
        if (idOpt.isEmpty()) {
            writeResponse(httpExchange, "Некорректный идентификатор задачи", 400);
            return;
        }
        String[] newTask = getInfoTask(httpExchange);
        if (!newTask[0].equals("null") && !newTask[1].equals("null") && !newTask[2].equals("null")) {
            httpExchange.sendResponseHeaders(200, 0);
            manager.updateTask(idOpt.get(), newTask[0], newTask[1], newTask[2]);
            String response = gson.toJson(manager.getTaskId(idOpt.get()));
            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write("Задача обновлена:\n".getBytes());
                os.write(response.getBytes());
            }
        } else {
            writeResponse(httpExchange, "Поля задачи не могут быть пустыми.", 400);
        }
        httpExchange.close();
    }

    private static void updateEpicHandler(HttpExchange httpExchange) throws IOException {
        Optional<Integer> idOpt = getTasksId(httpExchange);
        if (idOpt.isEmpty()) {
            writeResponse(httpExchange, "Некорректный идентификатор задачи", 400);
            return;
        }
        String[] newTask = getInfoTask(httpExchange);
        if (!newTask[0].equals("null") && !newTask[1].equals("null")) {
            httpExchange.sendResponseHeaders(200, 0);
            manager.updateEpic(idOpt.get(), newTask[0], newTask[1]);
            String response = gson.toJson(manager.getEpicId(idOpt.get()));
            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write("Задача обновлена:\n".getBytes());
                os.write(response.getBytes());
            }
        } else {
            writeResponse(httpExchange, "Поля задачи не могут быть пустыми.", 400);
        }
        httpExchange.close();
    }

    private static void updateSubHandler(HttpExchange httpExchange) throws IOException {
        Optional<Integer> idOpt = getTasksId(httpExchange);
        if (idOpt.isEmpty()) {
            writeResponse(httpExchange, "Некорректный идентификатор задачи", 400);
            return;
        }
        String[] newId = idOpt.toString().split("");
        String[] newTask = getInfoTask(httpExchange);
        Integer idEpic = Integer.parseInt(newId[0]);
        Integer idSub = Integer.parseInt(newId[1]);
        if (!newTask[0].equals("null") && !newTask[1].equals("null") && !newTask[2].equals("null")) {
            httpExchange.sendResponseHeaders(200, 0);
            manager.updateSubtask(idEpic, idSub, newTask[0], newTask[1], newTask[2]);
            String response = gson.toJson(manager.getTaskId(idOpt.get()));
            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write("Задача обновлена:\n".getBytes());
                os.write(response.getBytes());
            }
        } else {
            writeResponse(httpExchange, "Поля задачи не могут быть пустыми.", 400);
        }
        httpExchange.close();
    }

    private static void removeTaskHandler(HttpExchange httpExchange) throws IOException {
        manager.removeTask();
        if (manager.getTask().isEmpty()) {
            httpExchange.sendResponseHeaders(200, 0);
            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write("Задачи удалены.".getBytes());
            }
        } else {
            writeResponse(httpExchange, "Ошибка удаления.", 400);
        }
        httpExchange.close();
    }

    private static void removeEpicHandler(HttpExchange httpExchange) throws IOException {
        manager.removeEpic();
        if (manager.getEpic().isEmpty()) {
            httpExchange.sendResponseHeaders(200, 0);
            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write("Задачи удалены.".getBytes());
            }
        } else {
            writeResponse(httpExchange, "Ошибка удаления.", 400);
        }
        httpExchange.close();
    }


    private static void removeSubHandler(HttpExchange httpExchange) throws IOException {
        manager.removeSubtask();
        if (manager.getSubtask().isEmpty()) {
            httpExchange.sendResponseHeaders(200, 0);
            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write("Задачи удалены.".getBytes());
            }
        } else {
            writeResponse(httpExchange, "Ошибка удаления.", 400);
        }
        httpExchange.close();
    }

    private static void removeTaskIdHandler(HttpExchange httpExchange) throws IOException {
        Optional<Integer> idOpt = getTasksId(httpExchange);
        if (idOpt.isEmpty()) {
            writeResponse(httpExchange, "Некорректный идентификатор задачи", 400);
            return;
        }

        manager.removeTaskId(idOpt.get());
        for (Task task : manager.getTask()) {
            if (task.getId() == idOpt.get()) {
                writeResponse(httpExchange, "Ошибка удаления.", 400);
                return;
            }
        }
        httpExchange.sendResponseHeaders(200, 0);
        httpExchange.close();
    }

    private static void removeEpicIdHandler(HttpExchange httpExchange) throws IOException {
        Optional<Integer> idOpt = getTasksId(httpExchange);
        if (idOpt.isEmpty()) {
            writeResponse(httpExchange, "Некорректный идентификатор задачи", 400);
            return;
        }

        manager.removeEpicId(idOpt.get());
        for (Task task : manager.getEpic()) {
            if (task.getId() == idOpt.get()) {
                writeResponse(httpExchange, "Ошибка удаления.", 400);
                return;
            }
        }
        httpExchange.sendResponseHeaders(200, 0);
        httpExchange.close();
    }

    private static void removeSubIdHandler(HttpExchange httpExchange) throws IOException {
        Optional<Integer> idOpt = getTasksId(httpExchange);
        if (idOpt.isEmpty()) {
            writeResponse(httpExchange, "Некорректный идентификатор задачи", 400);
            return;
        }

        manager.removeSubtaskId(idOpt.get());
        for (Task task : manager.getSubtask()) {
            if (task.getId() == idOpt.get()) {
                writeResponse(httpExchange, "Ошибка удаления.", 400);
                return;
            }
        }
        httpExchange.sendResponseHeaders(200, 0);
        httpExchange.close();
    }
}




