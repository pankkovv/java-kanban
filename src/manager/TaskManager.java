package manager;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.List;

public interface TaskManager {

    List<Task> getTask();

    List<Epic> getEpic();

    List<Subtask> getSubtask();

    List<Subtask> getListAllSubtask(int idSearch);

    void removeTask();

    void removeEpic();

    void removeSubtask();

    Task getTaskId(int idSearch);

    Epic getEpicId(int idSearch);

    Subtask getSubtaskId(int idSearch);

    Task createTask(String title, String description, String status);

    Epic createEpic(String title, String description, String status);

    Subtask createSubtask(int idSearch, String title, String description, String status);

    void updateTask(int idSearch, String title, String description, String status);

    void updateEpic(int idSearch, String title, String description);

    void updateSubtask(int idSearchEpic, int idSearch, String title, String description, String status);

    void removeTaskId(int idSearch);

    void removeEpicId(int idSearch);

    void removeSubtaskId(int idSearch);

    int generatorId(String typeTask);

    void generatorStatusEpic(int idSearch);

    List<Task> getHistory();

    List<Task> getPrioritizedTasks();
}
