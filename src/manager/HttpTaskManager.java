package manager;


import api.KVTaskClient;
import com.google.gson.Gson;

import model.Epic;
import model.Subtask;
import model.Task;


import java.io.IOException;

import java.util.List;


public class HttpTaskManager extends FileBackedTasksManager {

    public Gson gson = new Gson();
    private String url;
    private KVTaskClient client;

    public HttpTaskManager(String url) throws IOException, InterruptedException {
        this.url = url;
        this.client = new KVTaskClient(url);
    }

    @Override
    public Task createTask(String title, String description, String status) {
        Task task = super.createTask(title, description, status);
        saveKV();
        return task;
    }

    @Override
    public Epic createEpic(String title, String description, String status) {
        Epic epic = super.createEpic(title, description, status);
        saveKV();
        return epic;
    }

    @Override
    public Subtask createSubtask(int idSearch, String title, String description, String status) {
        Subtask subtask = super.createSubtask(idSearch, title, description, status);
        saveKV();
        return subtask;
    }

    @Override
    public void updateTask(int idSearch, String title, String description, String status) {
        super.updateTask(idSearch, title, description, status);
        saveKV();
    }

    @Override
    public void updateEpic(int idSearch, String title, String description) {
        super.updateEpic(idSearch, title, description);
        saveKV();
    }

    @Override
    public void updateSubtask(int idSearchEpic, int idSearch, String title, String description, String status) {
        super.updateSubtask(idSearchEpic, idSearch, title, description, status);
        saveKV();
    }

    @Override
    public void removeTask() {
        super.removeTask();
        saveKV();
    }

    @Override
    public void removeEpic() {
        super.removeEpic();
        saveKV();
    }

    @Override
    public void removeSubtask() {
        super.removeSubtask();
        saveKV();
    }

    @Override
    public void removeTaskId(int idSearch) {
        super.removeTaskId(idSearch);
        saveKV();
    }

    @Override
    public void removeEpicId(int idSearch) {
        super.removeEpicId(idSearch);
        saveKV();
    }

    @Override
    public void removeSubtaskId(int idSearch) {
        super.removeSubtaskId(idSearch);
        saveKV();
    }

    @Override
    public List<Task> getHistory() {
        List<Task> list = super.getHistory();
        saveKV();
        return list;
    }

    public void saveKV() {
        try {
            for(Task task : super.getTask()){
                KVTaskClient.put("task?id="+task.getId(), gson.toJson(super.getTask()));
            }
            for(Epic epic : super.getEpic()){
                KVTaskClient.put("epic?id="+epic.getId(), gson.toJson(super.getEpic()));
            }
            for(Subtask subtask : super.getSubtask()){
                KVTaskClient.put("subtask?id="+subtask.getId(), gson.toJson(super.getSubtask()));
            }
                KVTaskClient.put("history?", gson.toJson(super.getHistory()));
        } catch (IOException e) {
            throw new FileBackedTasksManager.ManagerSaveException(e.getMessage());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public String loadKV(String key) throws IOException, InterruptedException {
        return KVTaskClient.load(key);
    }

}
