package api;

import com.google.gson.Gson;
import manager.FileBackedTasksManager;
import manager.HistoryManager;
import model.Epic;
import model.Subtask;
import model.Task;
import model.TypeTask;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.http.HttpClient;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


public class HttpTaskManager extends FileBackedTasksManager {

    private Gson gson = new Gson();
    private String url;

    private KVTaskClient client;

    public HttpTaskManager(String url) throws IOException, InterruptedException {
        this.url = url;
        this.client = new KVTaskClient(url);
    }

    @Override
    public Task createTask(String title, String description, String status) {
        Task task = super.createTask(title, description, status);
        save();
        return task;
    }

    @Override
    public Epic createEpic(String title, String description, String status) {
        Epic epic = super.createEpic(title, description, status);
        save();
        return epic;
    }

    @Override
    public Subtask createSubtask(int idSearch, String title, String description, String status) {
        Subtask subtask = super.createSubtask(idSearch, title, description, status);
        save();
        return subtask;
    }

    @Override
    public void updateTask(int idSearch, String title, String description, String status) {
        super.updateTask(idSearch, title, description, status);
        save();
    }

    @Override
    public void updateEpic(int idSearch, String title, String description) {
        super.updateEpic(idSearch, title, description);
        save();
    }

    @Override
    public void updateSubtask(int idSearchEpic, int idSearch, String title, String description, String status) {
        super.updateSubtask(idSearchEpic, idSearch, title, description, status);
        save();
    }

    @Override
    public void removeTask() {
        super.removeTask();
        save();
    }

    @Override
    public void removeEpic() {
        super.removeEpic();
        save();
    }

    @Override
    public void removeSubtask() {
        super.removeSubtask();
        save();
    }

    @Override
    public void removeTaskId(int idSearch) {
        super.removeTaskId(idSearch);
    }

    @Override
    public void removeEpicId(int idSearch) {
        super.removeEpicId(idSearch);
        save();
    }

    @Override
    public void removeSubtaskId(int idSearch) {
        super.removeSubtaskId(idSearch);
        save();
    }

    @Override
    public List<Task> getHistory() {
        List<Task> list = super.getHistory();
        save();
        return list;
    }

    public String taskToString(Task task) {
        String type = TypeTask.TASK.toString();
        String formatWrite = String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s%n", task.getId(), type, task.getTitle(), task.getStatus(), task.getDescription(), task.getTitle(), task.getStartTime(), task.getDuration(), task.getEndTime());
        return formatWrite;
    }

    public String epicToString(Epic epic) {
        String type = TypeTask.EPIC.toString();
        String formatWrite = String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s%n", epic.getId(), type, epic.getTitle(), epic.getStatus(), epic.getDescription(), epic.getTitle(), epic.getStartTime(), epic.getDuration(), epic.getEndTime());
        return formatWrite;
    }

    public String subtaskToString(Subtask subtask) {
        String type = TypeTask.SUB.toString();
        String formatWrite = String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s%n", subtask.getId(), type, subtask.getTitle(), subtask.getStatus(), subtask.getDescription(), subtask.getIdEpic(), subtask.getStartTime(), subtask.getDuration(), subtask.getEndTime());
        return formatWrite;
    }

    public Task taskFromString(String value) {
        String[] taskArray = value.split(",");
        Task task = createTask(taskArray[2], taskArray[4], taskArray[3]);
        return task;
    }

    public Epic epicFromString(String value) {
        String[] epicArray = value.split(",");
        Epic epic = createEpic(epicArray[2], epicArray[4], epicArray[3]);
        return epic;
    }

    public Subtask subtaskFromString(String value) {
        String[] subtaskArray = value.split(",");
        Subtask subtask = createSubtask(Integer.parseInt(subtaskArray[5]), subtaskArray[2], subtaskArray[4], subtaskArray[3]);
        return subtask;
    }

    public static String historyToString(HistoryManager manager) {
        StringBuilder formatWrite = new StringBuilder();
        for (int i = 0; i < manager.getHistory().size(); i++) {
            Task task = manager.getHistory().get(i);
            formatWrite.append(Integer.toString(task.getId()) + ",");
        }
        formatWrite.deleteCharAt(formatWrite.length() - 1);
        return formatWrite.toString();
    }

    public static List<Integer> historyFromString(String value) {
        String[] historyArray = value.split(",");
        List<Integer> historyList = new ArrayList<>();
        for (String id : historyArray) {
            historyList.add(Integer.parseInt(id));
        }
        return historyList;
    }

    @Override
    public void save() {
        try {
            KVTaskClient.put("task", gson.toJson(super.listTask));
            KVTaskClient.put("epic", gson.toJson(super.listEpic));
            KVTaskClient.put("subtask", gson.toJson(super.listSubtask));
            KVTaskClient.put("tasks", gson.toJson(super.getPrioritizedTasks()));
            KVTaskClient.put("history", gson.toJson(super.getHistory()));
        } catch (IOException e) {
            throw new FileBackedTasksManager.ManagerSaveException(e.getMessage());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static String load(String key) throws IOException, InterruptedException {
        return KVTaskClient.load(key);
    }

    public static class ManagerSaveException extends RuntimeException {
        public ManagerSaveException() {
        }

        public ManagerSaveException(final String message) {
            super(message);
        }
    }

}
