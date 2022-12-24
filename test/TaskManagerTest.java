import manager.FileBackedTasksManager;
import manager.TaskManager;
import model.Epic;
import model.Subtask;
import model.Task;

import java.util.List;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest <T extends TaskManager>{

    TaskManager manager = (T) new FileBackedTasksManager();

    //Test generator status method
    @Test
    public void generatorStatusForEpicWithoutSubtask(){
        Epic epic = manager.createEpic("Эпик-задачи", "Функция создания", "NEW");

        final List<Epic> tasks = manager.getEpic();
        final List<Subtask> subtasks = manager.getSubtask();

        assertNotNull(tasks, "Задачи на возвращаются.");
        assertNotNull(subtasks, "Задачи на возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(0, subtasks.size(), "Неверное количество задач.");

        manager.generatorStatusEpic(epic.getId());
        final String newEpicStatus = epic.getStatus();

        assertEquals("NEW", newEpicStatus, "Status изменился.");
    }

    @Test
    public void generatorStatusForEpicSubtaskStatusNew(){
        Epic epic = manager.createEpic("Эпик-задачи", "Функция создания", "NEW");
        Subtask subtask = manager.createSubtask(epic.getId(), "Подзадача", "Подзадача эпика", "NEW");
        Subtask subtask1 = manager.createSubtask(epic.getId(), "Подзадача", "Подзадача эпика", "NEW");
        Subtask subtask2 = manager.createSubtask(epic.getId(), "Подзадача", "Подзадача эпика", "NEW");
        manager.generatorStatusEpic(epic.getId());
        final String newEpicStatus = epic.getStatus();

        assertEquals("NEW", newEpicStatus, "Status изменился.");
    }

    @Test
    public void generatorStatusForEpicSubtaskStatusInProgress(){
        Epic epic = manager.createEpic("Эпик-задачи", "Функция создания", "NEW");
        Subtask subtask = manager.createSubtask(epic.getId(), "Подзадача", "Подзадача эпика", "IN_PROGRESS");
        Subtask subtask1 = manager.createSubtask(epic.getId(), "Подзадача", "Подзадача эпика", "IN_PROGRESS");
        Subtask subtask2 = manager.createSubtask(epic.getId(), "Подзадача", "Подзадача эпика", "IN_PROGRESS");
        manager.generatorStatusEpic(epic.getId());
        final String newEpicStatus = epic.getStatus();

        assertEquals("IN_PROGRESS", newEpicStatus, "Status не изменился.");
    }

    @Test
    public void generatorStatusForEpicSubtaskStatusDone(){
        Epic epic = manager.createEpic("Эпик-задачи", "Функция создания", "NEW");
        Subtask subtask = manager.createSubtask(epic.getId(), "Подзадача", "Подзадача эпика", "DONE");
        Subtask subtask1 = manager.createSubtask(epic.getId(), "Подзадача 1", "Подзадача эпика", "DONE");
        Subtask subtask2 = manager.createSubtask(epic.getId(), "Подзадача 2", "Подзадача эпика", "DONE");

        manager.generatorStatusEpic(epic.getId());
        final String newEpicStatus = epic.getStatus();

        assertEquals("DONE", newEpicStatus, "Status не изменился.");
    }

    @Test
    public void generatorStatusForEpicSubtaskStatusNewDone(){
        Epic epic = manager.createEpic("Эпик-задачи", "Функция создания", "NEW");
        Subtask subtask = manager.createSubtask(epic.getId(), "Подзадача", "Подзадача эпика", "NEW");
        Subtask subtask1 = manager.createSubtask(epic.getId(), "Подзадача 1", "Подзадача эпика", "DONE");
        Subtask subtask2 = manager.createSubtask(epic.getId(), "Подзадача 2", "Подзадача эпика", "NEW");

        manager.generatorStatusEpic(epic.getId());
        final String newEpicStatus = epic.getStatus();

        assertEquals("IN_PROGRESS", newEpicStatus, "Status не изменился.");
    }

    //Test getEndTimeEpic method
    @Test
    public void getEndTimeEpicNEW(){
        Epic epic = manager.createEpic("Эпик-задачи", "Функция создания", "NEW");
        Subtask subtask = manager.createSubtask(epic.getId(), "Подзадача 1", "Подзадача эпика", "NEW");

        assertNotNull(epic, "Задача не создана.");
        assertNotNull(subtask, "Задача не создана.");
        assertNotNull(epic.getStartTime(), "Нет времени старта.");
        assertNotNull(subtask.getStartTime(), "Нет времени старта.");
        assertNull(epic.getEndTime(), "Время окончания посчитано не верно.");
        assertNull(subtask.getEndTime(), "Время окончания посчитано не верно.");
    }


    @Test
    public void getEndTimeEpicINPROGRESS(){
        Epic epic = manager.createEpic("Эпик-задачи", "Функция создания", "NEW");
        Subtask subtask = manager.createSubtask(epic.getId(), "Подзадача 1", "Подзадача эпика", "IN_PROGRESS");

        assertNotNull(epic, "Задача не создана.");
        assertNotNull(subtask, "Задача не создана.");
        assertEquals("IN_PROGRESS", epic.getStatus(), "Неправильный статус.");
        assertNotNull(epic.getStartTime(), "Нет времени старта.");
        assertNotNull(subtask.getStartTime(), "Нет времени старта.");
        assertNull(epic.getEndTime(), "Время окончания посчитано не верно.");
        assertNull(subtask.getEndTime(), "Время окончания посчитано не верно.");
    }

    @Test
    public void getEndTimeEpicDONE(){
        Long[] streamTimeout = new Long[1000];
        Epic epic = manager.createEpic("Эпик-задачи", "Функция создания", "NEW");
        Subtask subtask0 = manager.createSubtask(epic.getId(), "Подзадача 1", "Подзадача эпика", "NEW");
        Subtask subtask1 = manager.createSubtask(epic.getId(), "Подзадача 1", "Подзадача эпика", "NEW");
        Subtask subtask2 = manager.createSubtask(epic.getId(), "Подзадача 1", "Подзадача эпика", "NEW");

        assertNotNull(epic, "Задача не создана.");
        assertNotNull(subtask0, "Задача не создана.");
        assertNotNull(subtask1, "Задача не создана.");
        assertNotNull(subtask2, "Задача не создана.");

        manager.updateSubtask(epic.getId(), subtask0.getId(), "Подзадача NEW", "Подзадача эпика", "DONE");
        for(Long i : streamTimeout){
            System.out.println(" ");
        }
        manager.updateSubtask(epic.getId(), subtask1.getId(), "Подзадача NEW", "Подзадача эпика", "DONE");
        for(Long i : streamTimeout){
            System.out.println(" ");
        }
        for(Long i : streamTimeout){
            System.out.println(" ");
        }
        manager.updateSubtask(epic.getId(), subtask2.getId(), "Подзадача NEW", "Подзадача эпика", "DONE");

        Duration expectedDuration = subtask0.getDuration().plus(subtask1.getDuration()).plus(subtask2.getDuration());
        LocalDateTime expectedTime = subtask0.getStartTime().plus(expectedDuration);

        assertNotNull(epic.getStartTime(), "Нет времени старта.");
        assertNotNull(subtask0.getStartTime(), "Нет времени старта.");
        assertNotNull(subtask1.getStartTime(), "Нет времени старта.");
        assertNotNull(subtask2.getStartTime(), "Нет времени старта.");
        assertNotNull(epic.getEndTime(), "Время окончания посчитано не верно.");
        assertNotNull(subtask0.getEndTime(), "Время окончания посчитано не верно.");
        assertNotNull(subtask1.getEndTime(), "Время окончания посчитано не верно.");
        assertNotNull(subtask2.getEndTime(), "Время окончания посчитано не верно.");
        assertEquals(epic.getStartTime(), subtask0.getStartTime(), "Время начала epic записано не верно.");
        assertEquals(expectedTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd||HH:mm:ss")), epic.getEndTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd||HH:mm:ss")), "Время окончания epic записано не верно.");
    }


    //Со стандартным поведением.
    @Test
    public void createNewTask() {
        Task task = manager.createTask("Задача", "Функция создания задачи", "NEW");
        final Task savedTask = manager.getTaskId(task.getId());

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = manager.getTask();

        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    public void createNewEpic() {
        Epic epic = manager.createEpic("Эпик-задача", "Функция создания", "NEW");
        final Epic savedTask = manager.getEpicId(epic.getId());

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(epic, savedTask, "Задачи не совпадают.");

        final List<Epic> tasks = manager.getEpic();

        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(epic, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    public void createNewSubtask() {
        Epic epic = manager.createEpic("Эпик-задачи", "Функция создания", "NEW");
        Subtask subtask = manager.createSubtask(epic.getId(), "Подзадача 1", "Подзадача эпика", "NEW");
        final Subtask savedTask = manager.getSubtaskId(subtask.getId());

        assertNotNull(savedTask, "Задача не найдена.");
        assertNotNull(savedTask.getIdEpic(), "Эпик-задача не найдена.");
        assertEquals(subtask, savedTask, "Задачи не совпадают.");

        final List<Subtask> tasks = manager.getSubtask();

        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(subtask, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    public void updateExistingTask() {
        Task task = manager.createTask("Задача", "Функция создания задачи", "NEW");
        final String savedTask = manager.getTaskId(task.getId()).toString();

        manager.updateTask(task.getId(), "Задача NEW", "Функция создания задачи", "DONE");
        final String savedUpdateTask = manager.getTaskId(task.getId()).toString();

        assertNotNull(savedTask, "Задача не найдена.");
        assertNotNull(savedUpdateTask, "Задача не найдена.");
        assertNotEquals(savedTask, savedUpdateTask, "Задачи совпадают.");

        final List<Task> tasks = manager.getTask();

        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(savedUpdateTask, tasks.get(0).toString(), "Задачи не совпадают.");
    }

    @Test
    public void updateExistingEpic() {
        Epic epic = manager.createEpic("Эпик-задача", "Функция создания", "NEW");
        final String savedTask = manager.getEpicId(epic.getId()).toString();

        manager.updateEpic(epic.getId(), "Эпик-задача NEW", "Функция создания задачи");
        final String savedUpdateTask = manager.getEpicId(epic.getId()).toString();

        assertNotNull(savedTask, "Задача не найдена.");
        assertNotNull(savedUpdateTask, "Задача не найдена.");
        assertNotEquals(savedTask, savedUpdateTask, "Задачи совпадают.");

        final List<Epic> tasks = manager.getEpic();

        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(savedUpdateTask, tasks.get(0).toString(), "Задачи не совпадают.");
    }

    @Test
    public void updateExistingSubtask() {
        Epic epic = manager.createEpic("Эпик-задачи", "Функция создания", "NEW");
        Subtask subtask = manager.createSubtask(epic.getId(), "Подзадача", "Подзадача эпика", "NEW");
        final String savedTask = manager.getSubtaskId(subtask.getId()).toString();

        manager.updateSubtask(epic.getId(), subtask.getId(), "Подзадача NEW", "Подзадача эпика", "DONE");
        final String savedUpdateTask = manager.getSubtaskId(subtask.getId()).toString();

        assertNotNull(savedTask, "Задача не найдена.");
        assertNotNull(savedUpdateTask, "Задача не найдена.");
        assertNotNull(subtask.getIdEpic(), "Эпик-задача не найдена.");
        assertNotEquals(savedTask, savedUpdateTask, "Задачи совпадают.");

        final List<Subtask> tasks = manager.getSubtask();

        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(savedUpdateTask, tasks.get(0).toString(), "Задачи не совпадают.");
    }

    @Test
    public void removeExistingTaskId() {
        Task task = manager.createTask("Задача", "Функция создания задачи", "NEW");

        final List<Task> tasks = manager.getTask();

        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");

        manager.removeTaskId(task.getId());
        assertEquals(0, tasks.size(), "Неверное количество задач.");
    }

    @Test
    public void removeExistingEpicId() {
        Epic epic = manager.createEpic("Эпик-задачи", "Функция создания", "NEW");
        Subtask subtask = manager.createSubtask(epic.getId(), "Подзадача", "Подзадача эпика", "NEW");

        final List<Epic> tasks = manager.getEpic();
        final List<Subtask> subtasksListEpic = manager.getSubtask();


        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(1, subtasksListEpic.size(), "Неверное количество задач.");

        manager.removeEpicId(epic.getId());
        assertEquals(0, tasks.size(), "Неверное количество задач.");
        assertEquals(0, subtasksListEpic.size(), "Неверное количество задач.");
    }

    @Test
    public void removeExistingSubtaskId() {
        Epic epic = manager.createEpic("Эпик-задачи", "Функция создания", "NEW");
        Subtask subtask = manager.createSubtask(epic.getId(), "Подзадача", "Подзадача эпика", "NEW");

        final List<Subtask> tasks = manager.getSubtask();
        final List<Subtask> subtasksListEpic = epic.getListOfSubtasks();

        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(1, subtasksListEpic.size(), "Неверное количество задач.");

        manager.removeSubtaskId(subtask.getId());
        assertEquals(0, tasks.size(), "Неверное количество задач.");
        assertEquals(0, subtasksListEpic.size(), "Неверное количество задач.");
    }

    @Test
    public void removeExistingTask() {
        Task task = manager.createTask("Задача", "Функция создания задачи", "NEW");
        Task taskNew = manager.createTask("Задача", "Функция создания задачи", "NEW");

        final List<Task> tasks = manager.getTask();

        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(2, tasks.size(), "Неверное количество задач.");

        manager.removeTask();
        assertEquals(0, tasks.size(), "Неверное количество задач.");
    }

    @Test
    public void removeExistingEpic() {
        Epic epic = manager.createEpic("Эпик-задачи", "Функция создания", "NEW");
        Epic epicNew = manager.createEpic("Эпик-задачи", "Функция создания", "NEW");
        Subtask subtask = manager.createSubtask(epic.getId(), "Подзадача", "Подзадача эпика", "NEW");

        final List<Epic> tasks = manager.getEpic();
        final List<Subtask> subtasksListEpic = manager.getSubtask();

        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(2, tasks.size(), "Неверное количество задач.");

        manager.removeEpic();
        assertEquals(0, tasks.size(), "Неверное количество задач.");
        assertEquals(0, subtasksListEpic.size(), "Неверное количество задач.");
    }

    @Test
    public void removeExistingSubtask() {
        Epic epic = manager.createEpic("Эпик-задачи", "Функция создания", "NEW");
        Subtask subtask = manager.createSubtask(epic.getId(), "Подзадача", "Подзадача эпика", "NEW");
        Subtask subtaskNew = manager.createSubtask(epic.getId(), "Подзадача", "Подзадача эпика", "NEW");

        final List<Subtask> tasks = manager.getSubtask();
        final List<Subtask> subtasksListEpic = manager.getListAllSubtask(epic.getId());

        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(2, tasks.size(), "Неверное количество задач.");
        assertEquals(2, subtasksListEpic.size(), "Неверное количество задач.");

        manager.removeSubtask();
        assertEquals(0, tasks.size(), "Неверное количество задач.");
        assertEquals(0, subtasksListEpic.size(), "Неверное количество задач.");
    }

    @Test
    public void generatorIdForTasks(){
        Task task = manager.createTask("Задача", "Функция создания задачи", "NEW");
        Epic epic = manager.createEpic("Эпик-задачи", "Функция создания", "NEW");
        Subtask subtask = manager.createSubtask(epic.getId(), "Подзадача", "Подзадача эпика", "NEW");

        assertNotNull(task.getId(), "Неверный id Task.");
        assertNotNull(epic.getId(), "Неверный id Epic.");
        assertNotNull(subtask.getId(), "Неверный id Subtask.");
    }

    @Test
    public void getHistoryTest(){
        Task task = manager.createTask("Задача", "Функция создания задачи", "NEW");
        Epic epic = manager.createEpic("Эпик-задачи", "Функция создания", "NEW");
        Subtask subtask = manager.createSubtask(epic.getId(), "Подзадача", "Подзадача эпика", "NEW");
        manager.getTaskId(task.getId());
        manager.getEpicId(epic.getId());
        manager.getSubtaskId(subtask.getId());
        String expectedString ="[" + task.toString() + ", " + epic.toString() + ", " + subtask.toString() + "]";

        assertNotNull(task, "Задача не создана");
        assertNotNull(epic, "Задача не создана");
        assertNotNull(subtask, "Задача не создана");
        assertEquals(expectedString, manager.getHistory().toString(), "Неверное отображение задачи.");
    }

    @Test
    public void getPrioritizedTasksTest(){
        Task task = manager.createTask("Задача", "Функция создания задачи", "NEW");
        task.setStartTime(LocalDateTime.of(2022, 12, 24, 14, 00, 00));

        Task task4 = manager.createTask("Задача", "Функция создания задачи", "NEW");
        task4.setStartTime(LocalDateTime.of(2022, 12, 24, 14, 00, 00));
        task4.setEndTime(task.getEndTime());
        task4.setId(task.getId());

        Task task1 = manager.createTask("Задача 1", "Функция создания задачи 1", "DONE");
        task1.setStartTime(LocalDateTime.of(2022, 12, 24, 14, 01, 00));
        Task task2 = manager.createTask("Задача 2", "Функция создания задачи 2", "DONE");
        task2.setStartTime(LocalDateTime.of(2022, 12, 24, 14, 02, 00));

        Epic epic = manager.createEpic("Эпик-задачи", "Функция создания", "NEW");

        Subtask subtask = manager.createSubtask(epic.getId(), "Подзадача", "Подзадача эпика", "DONE");
        subtask.setStartTime(LocalDateTime.of(2022, 12, 24, 14, 03, 00));
        Subtask subtask1 = manager.createSubtask(epic.getId(), "Подзадача 1", "Подзадача эпика 1", "DONE");
        subtask1.setStartTime(LocalDateTime.of(2022, 12, 24, 14, 04, 00));
        Subtask subtask2 = manager.createSubtask(epic.getId(), "Подзадача 2", "Подзадача эпика 2", "DONE");
        subtask2.setStartTime(LocalDateTime.of(2022, 12, 24, 14, 05, 00));

        manager.getPrioritizedTasks();
        System.out.println("");


        manager.updateTask(task.getId(), "kfgnfkdmgkd", "dsgfshdfkjsdfjsdhjfk", "DONE");
        manager.updateTask(task4.getId(), "kfgnfkdmgkd", "dsgfshdfkjsdfjsdhjfk", "DONE");
        subtask1.setStartTime(null);
        manager.getPrioritizedTasks();
        System.out.println("");

        manager.updateTask(task1.getId(), "sdhhfgksdhfidsj", "dsgfshdfkjsdfjsdhjfk", "DONE");
        task1.setStartTime(null);

        manager.getPrioritizedTasks();
        System.out.println("");
    }

    //С пустым списком задач.
    @Test
    public void createNewTaskEmpty() {
        Task task = manager.createTask("Задача", "Функция создания задачи", "NEW");
        manager.removeTaskId(task.getId());
        final Task savedTask = manager.getTaskId(task.getId());

        assertNull(savedTask, "Задача не найдена.");
        assertNotEquals(task, savedTask, "Задачи совпадают.");

        final List<Task> tasks = manager.getTask();

        assertEquals(0, tasks.size(), "Неверное количество задач.");
    }

    @Test
    public void createNewEpicEmpty() {
        Epic epic = manager.createEpic("Эпик-задача", "Функция создания", "NEW");
        manager.removeEpicId(epic.getId());
        final Epic savedTask = manager.getEpicId(epic.getId());

        assertNull(savedTask, "Задача не найдена.");
        assertNotEquals(epic, savedTask, "Задачи совпадают.");

        final List<Epic> tasks = manager.getEpic();

        assertEquals(0, tasks.size(), "Неверное количество задач.");
    }

    @Test
    public void createNewSubtaskEmpty() {
        Epic epic = manager.createEpic("Эпик-задача", "Функция создания", "NEW");
        Subtask subtask = manager.createSubtask(epic.getId(), "Подзадача 1", "Подзадача эпика", "NEW");
        manager.removeSubtaskId(subtask.getId());
        final Subtask savedTask = manager.getSubtaskId(subtask.getId());

        assertNull(savedTask, "Задача не найдена.");
        assertNotEquals(subtask, savedTask, "Задачи совпадают.");

        final List<Subtask> tasks = manager.getSubtask();

        assertEquals(0, tasks.size(), "Неверное количество задач.");
    }

    @Test
    public void updateExistingTaskEmpty() {
        Task task = manager.createTask("Задача", "Функция создания задачи", "NEW");
        final Task savedTask = manager.getTaskId(task.getId());

        manager.removeTask();
        manager.updateTask(task.getId(), "Задача NEW", "Функция создания задачи", "DONE");
        final Task savedUpdateTask = manager.getTaskId(task.getId());

        assertNotNull(savedTask, "Задача не найдена.");
        assertNull(savedUpdateTask, "Несуществующая задача найдена.");
        assertNotEquals(savedTask, savedUpdateTask, "Задачи совпадают.");

        final List<Task> tasks = manager.getTask();

        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(0, tasks.size(), "Неверное количество задач.");
    }

    @Test
    public void updateExistingEpicEmpty() {
        Epic epic = manager.createEpic("Эпик-задача", "Функция создания", "NEW");
        final Epic savedTask = manager.getEpicId(epic.getId());

        manager.removeEpic();
        manager.updateEpic(epic.getId(), "Эпик-задача NEW", "Функция создания задачи");
        final Epic savedUpdateTask = manager.getEpicId(epic.getId());

        assertNotNull(savedTask, "Задача не найдена.");
        assertNull(savedUpdateTask, "Несуществующая задача найдена.");
        assertNotEquals(savedTask, savedUpdateTask, "Задачи совпадают.");

        final List<Epic> tasks = manager.getEpic();

        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(0, tasks.size(), "Неверное количество задач.");
    }

    @Test
    public void updateExistingSubtaskEmpty() {
        Epic epic = manager.createEpic("Эпик-задачи", "Функция создания", "NEW");
        Subtask subtask = manager.createSubtask(epic.getId(), "Подзадача", "Подзадача эпика", "NEW");
        final Subtask savedTask = manager.getSubtaskId(subtask.getId());

        manager.removeSubtask();
        manager.updateSubtask(epic.getId(), subtask.getId(), "Подзадача NEW", "Подзадача эпика", "DONE");
        final Subtask savedUpdateTask = manager.getSubtaskId(subtask.getId());

        assertNotNull(savedTask, "Задача не найдена.");
        assertNull(savedUpdateTask, "Несуществующая задача найдена.");
        assertNotNull(subtask.getIdEpic(), "Эпик-задача не найдена.");
        assertNotEquals(savedTask, savedUpdateTask, "Задачи совпадают.");

        final List<Subtask> tasks = manager.getSubtask();

        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(0, tasks.size(), "Неверное количество задач.");
    }

    @Test
    public void removeExistingTaskIdEmpty() {
        Task task = manager.createTask("Задача", "Функция создания задачи", "NEW");

        final List<Task> tasks = manager.getTask();

        manager.removeTask();
        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(0, tasks.size(), "Неверное количество задач.");

        manager.removeTaskId(task.getId());
        assertEquals(0, tasks.size(), "Неверное количество задач.");
    }

    @Test
    public void removeExistingEpicIdEmpty() {
        Epic epic = manager.createEpic("Эпик-задачи", "Функция создания", "NEW");
        Subtask subtask = manager.createSubtask(epic.getId(), "Подзадача", "Подзадача эпика", "NEW");

        final List<Epic> tasks = manager.getEpic();
        final List<Subtask> subtasksListEpic = manager.getSubtask();

        manager.removeEpic();
        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(0, tasks.size(), "Неверное количество задач.");
        assertEquals(0, subtasksListEpic.size(), "Неверное количество задач.");

        manager.removeEpicId(epic.getId());
        assertEquals(0, tasks.size(), "Неверное количество задач.");
        assertEquals(0, subtasksListEpic.size(), "Неверное количество задач.");
    }

    @Test
    public void removeExistingSubtaskIdEmpty() {
        Epic epic = manager.createEpic("Эпик-задачи", "Функция создания", "NEW");
        Subtask subtask = manager.createSubtask(epic.getId(), "Подзадача", "Подзадача эпика", "NEW");

        final List<Subtask> tasks = manager.getSubtask();
        final List<Subtask> subtasksListEpic = epic.getListOfSubtasks();

        manager.removeSubtask();
        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(0, tasks.size(), "Неверное количество задач.");
        assertEquals(0, subtasksListEpic.size(), "Неверное количество задач.");

        manager.removeSubtaskId(subtask.getId());
        assertEquals(0, tasks.size(), "Неверное количество задач.");
        assertEquals(0, subtasksListEpic.size(), "Неверное количество задач.");
    }

    @Test
    public void removeExistingTaskEmpty() {
        final List<Task> tasks = manager.getTask();

        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(0, tasks.size(), "Неверное количество задач.");

        manager.removeTask();
        assertEquals(0, tasks.size(), "Неверное количество задач.");
    }

    @Test
    public void removeExistingEpicEmpty() {
        final List<Epic> tasks = manager.getEpic();

        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(0, tasks.size(), "Неверное количество задач.");

        manager.removeEpic();
        assertEquals(0, tasks.size(), "Неверное количество задач.");
    }

    @Test
    public void removeExistingSubtaskEmpty() {
        final List<Subtask> tasks = manager.getSubtask();

        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(0, tasks.size(), "Неверное количество задач.");

        manager.removeSubtask();
        assertEquals(0, tasks.size(), "Неверное количество задач.");
    }

    @Test
    public void getHistoryEmpty(){
        final List<Task> tasks = manager.getTask();
        final List<Epic> epics = manager.getEpic();
        final List<Subtask> subs = manager.getSubtask();

        assertNotNull(tasks, "Задачи на возвращаются.");
        assertNotNull(epics, "Задачи на возвращаются.");
        assertNotNull(subs, "Задачи на возвращаются.");
        assertEquals(List.of(), manager.getHistory(), "Неверное отображение задачи.");
    }


    //С неверным идентификатором задачи (пустой и/или несуществующий идентификатор).
    @Test
    public void createNewTaskWrong() {
        Task task = manager.createTask("Задача", "Функция создания задачи", "NEW");
        final Task savedTask = manager.getTaskId(task.getId());
        savedTask.setId(null);

        System.out.println(savedTask);
        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = manager.getTask();

        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    public void createNewEpicWrong() {
        Epic epic = manager.createEpic("Эпик-задача", "Функция создания", "NEW");
        final Epic savedTask = manager.getEpicId(epic.getId());
        epic.setId(null);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(epic, savedTask, "Задачи не совпадают.");

        final List<Epic> tasks = manager.getEpic();

        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(epic, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    public void createNewSubtaskWrong() {
        Epic epic = manager.createEpic("Эпик-задачи", "Функция создания", "NEW");
        Subtask subtask = manager.createSubtask(epic.getId(), "Подзадача 1", "Подзадача эпика", "NEW");
        final Subtask savedTask = manager.getSubtaskId(subtask.getId());
        savedTask.setId(null);

        assertNotNull(savedTask, "Задача не найдена.");
        assertNotNull(savedTask.getIdEpic(), "Эпик-задача не найдена.");
        assertEquals(subtask, savedTask, "Задачи не совпадают.");

        final List<Subtask> tasks = manager.getSubtask();

        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(subtask, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    public void updateExistingTaskWrong() {
        Task task = manager.createTask("Задача", "Функция создания задачи", "NEW");
        final Task savedTask = manager.getTaskId(task.getId());
        savedTask.setId(null);


        final NullPointerException exception = assertThrows(NullPointerException.class, new Executable() {
            @Override
            public void execute() {
                manager.updateTask(savedTask.getId(), "Задача NEW", "Функция создания задачи", "DONE");
            }
        });

        assertNotNull(manager.getTask().get(0), "Задача не найдена");
        assertNull(exception.getMessage());
    }

    @Test
    public void updateExistingEpicWrong() {
        Epic epic = manager.createEpic("Эпик-задача", "Функция создания", "NEW");
        final Epic savedTask = manager.getEpicId(epic.getId());
        savedTask.setId(null);

        final NullPointerException exception = assertThrows(NullPointerException.class, new Executable() {
            @Override
            public void execute() {
                manager.updateEpic(savedTask.getId(), "Эпик-задача NEW", "Функция создания задачи");
            }
        });

        assertNotNull(manager.getEpic().get(0), "Задача не найдена");
        assertNull(exception.getMessage());

    }

    @Test
    public void updateExistingSubtaskWrong() {
        Epic epic = manager.createEpic("Эпик-задачи", "Функция создания", "NEW");
        Subtask subtask = manager.createSubtask(epic.getId(), "Подзадача", "Подзадача эпика", "NEW");
        final Subtask savedTask = manager.getSubtaskId(subtask.getId());
        savedTask.setId(null);

        final NullPointerException exception = assertThrows(NullPointerException.class, new Executable() {
            @Override
            public void execute() {
                manager.updateSubtask(epic.getId(), savedTask.getId(), "Подзадача NEW", "Подзадача эпика", "DONE");
            }
        });

        assertNotNull(manager.getSubtask().get(0), "Задача не найдена");
        assertNull(exception.getMessage());
    }

    @Test
    public void removeExistingTaskIdWrong() {
        Task task = manager.createTask("Задача", "Функция создания задачи", "NEW");
        task.setId(null);

        final NullPointerException exception = assertThrows(NullPointerException.class, new Executable() {
            @Override
            public void execute() {
                manager.removeTaskId(task.getId());
            }
        });

        final List<Task> tasks = manager.getTask();

        assertNotNull(tasks, "Задачи на возвращаются.");
        assertNull(exception.getMessage());
        assertEquals(1, tasks.size(), "Неверное количество задач.");
    }

    @Test
    public void removeExistingEpicIdWrong() {
        Epic epic = manager.createEpic("Эпик-задачи", "Функция создания", "NEW");
        Subtask subtask = manager.createSubtask(epic.getId(), "Подзадача", "Подзадача эпика", "NEW");
        epic.setId(null);

        final NullPointerException exception = assertThrows(NullPointerException.class, new Executable() {
            @Override
            public void execute() {
                manager.removeEpicId(epic.getId());
            }
        });

        final List<Epic> tasks = manager.getEpic();
        final List<Subtask> subtasksListEpic = manager.getSubtask();

        assertNotNull(tasks, "Задачи на возвращаются.");
        assertNotNull(subtasksListEpic, "Задачи на возвращаются.");
        assertNull(exception.getMessage());
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(1, subtasksListEpic.size(), "Неверное количество задач.");
    }

    @Test
    public void removeExistingSubtaskIdWrong() {
        Epic epic = manager.createEpic("Эпик-задачи", "Функция создания", "NEW");
        Subtask subtask = manager.createSubtask(epic.getId(), "Подзадача", "Подзадача эпика", "NEW");
        subtask.setId(null);

        final NullPointerException exception = assertThrows(NullPointerException.class, new Executable() {
            @Override
            public void execute() {
                manager.removeSubtaskId(subtask.getId());
            }
        });

        final List<Subtask> tasks = manager.getSubtask();
        final List<Subtask> subtasksListEpic = manager.getListAllSubtask(epic.getId());

        assertNotNull(tasks, "Задачи на возвращаются.");
        assertNotNull(subtasksListEpic, "Задачи на возвращаются.");
        assertNull(exception.getMessage());
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
    }

    @Test
    public void removeExistingTaskWrong() {
        Task task = manager.createTask("Задача", "Функция создания задачи", "NEW");
        Task taskNew = manager.createTask("Задача", "Функция создания задачи", "NEW");
        task.setId(null);
        taskNew.setId(null);

        final List<Task> tasks = manager.getTask();

        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(2, tasks.size(), "Неверное количество задач.");

        final NullPointerException exception = assertThrows(NullPointerException.class, new Executable() {
            @Override
            public void execute() {
                manager.removeTask();
            }
        });

        assertNull(exception.getMessage());
        assertEquals(2, tasks.size(), "Неверное количество задач.");
    }

    @Test
    public void removeExistingEpicWrong() {
        Epic epic = manager.createEpic("Эпик-задачи", "Функция создания", "NEW");
        Epic epicNew = manager.createEpic("Эпик-задачи", "Функция создания", "NEW");
        Subtask subtask = manager.createSubtask(epic.getId(), "Подзадача", "Подзадача эпика", "NEW");
        epic.setId(null);

        final List<Epic> tasks = manager.getEpic();
        final List<Subtask> subtasksListEpic = manager.getSubtask();


        assertNotNull(tasks, "Задачи на возвращаются.");
        assertNotNull(subtasksListEpic, "Задачи на возвращаются.");
        assertEquals(2, tasks.size(), "Неверное количество задач.");
        assertEquals(1, subtasksListEpic.size(), "Неверное количество задач.");

        final NullPointerException exception = assertThrows(NullPointerException.class, new Executable() {
            @Override
            public void execute() {
                manager.removeEpic();
            }
        });

        assertNull(exception.getMessage());
        assertEquals(2, tasks.size(), "Неверное количество задач.");
        assertEquals(0, subtasksListEpic.size(), "Неверное количество задач.");
    }

    @Test
    public void removeExistingSubtaskWrong() {
        Epic epic = manager.createEpic("Эпик-задачи", "Функция создания", "NEW");
        Subtask subtask = manager.createSubtask(epic.getId(), "Подзадача", "Подзадача эпика", "NEW");
        Subtask subtaskNew = manager.createSubtask(epic.getId(), "Подзадача NEW", "Подзадача эпика", "NEW");
        subtask.setId(null);
        subtaskNew.setId(null);

        final List<Subtask> tasks = manager.getSubtask();
        final List<Subtask> subtasksListEpic = manager.getListAllSubtask(epic.getId());

        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(2, tasks.size(), "Неверное количество задач.");
        assertEquals(2, subtasksListEpic.size(), "Неверное количество задач.");

        final NullPointerException exception = assertThrows(NullPointerException.class, new Executable() {
            @Override
            public void execute() {
                manager.removeSubtask();
            }
        });

        assertNull(exception.getMessage());
        assertEquals(2, tasks.size(), "Неверное количество задач.");
        assertEquals(2, subtasksListEpic.size(), "Неверное количество задач.");
    }

    @Test
    public void generatorIdForTasksWrong(){
        Task task = manager.createTask("Задача", "Функция создания задачи", "NEW");
        Epic epic = manager.createEpic("Эпик-задачи", "Функция создания", "NEW");
        Subtask subtask = manager.createSubtask(epic.getId(), "Подзадача", "Подзадача эпика", "NEW");
        task.setId(null);
        epic.setId(null);
        subtask.setId(null);

        task.setId(manager.generatorId("TASK"));
        epic.setId(manager.generatorId("EPIC"));
        subtask.setId(manager.generatorId("SUB"));

        assertNotNull(task.getId(), "Неверный id Task.");
        assertNotNull(epic.getId(), "Неверный id Epic.");
        assertNotNull(subtask.getId(), "Неверный id Subtask.");
    }

    @Test
    public void getHistoryWrong(){
        Task task = manager.createTask("Задача", "Функция создания задачи", "NEW");
        Epic epic = manager.createEpic("Эпик-задачи", "Функция создания", "NEW");
        Subtask subtask = manager.createSubtask(epic.getId(), "Подзадача", "Подзадача эпика", "NEW");
        task.setId(null);
        epic.setId(null);
        subtask.setId(null);
        manager.getTask();
        manager.getEpic();
        manager.getSubtask();

        assertNotNull(task, "Задачи на возвращаются.");
        assertNotNull(epic, "Задачи на возвращаются.");
        assertNotNull(subtask, "Задачи на возвращаются.");

        assertEquals(List.of(), manager.getHistory(), "История запронена неправильно.");
    }

}

