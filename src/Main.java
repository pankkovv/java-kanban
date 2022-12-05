import manager.Managers;
import manager.TaskManager;
import model.Epic;
import model.Subtask;
import model.Task;


public class Main {

    public static void main(String[] args) {

        TaskManager manager = Managers.getDefault();

        System.out.println("Тест содания задач:");
        Task task0 = manager.createTask("Создать задачу", "Реализация функции создания задачи", "NEW");
        Task task1 = manager.createTask("Создать еще одну задачу", "Реализация функции создания задачи вторая попытка", "NEW");
        Task task2 = manager.createTask("Создать задачу №2", "Реализация функции менеджера по созданию задачи", "NEW");
        System.out.println();
        System.out.println("Тест создания task.Epic-задачи:");
        Epic epic0 = manager.createEpic("Создание эпик-задачи", "Тест реализации функции создания", "NEW");
        Epic epic1 = manager.createEpic("Создание эпик-задачи №1", "Тест реализации функции создания", "NEW");
        Epic epic2 = manager.createEpic("Создание эпик-задачи №2", "Тест реализации функции создания", "NEW");
        System.out.println();
        System.out.println("Тест создания task.Subtask-задачи:");
        Subtask subtask0 = manager.createSubtask(epic0.getId(), "Подзадача 1", "Это 1-я подзадача эпика №1", "DONE");
        Subtask subtask1 = manager.createSubtask(epic0.getId(), "Подзадача 2", "Это 2-я подзадача эпика №1", "DONE");
        Subtask subtask2 = manager.createSubtask(epic0.getId(), "Подзадача 3", "Это 3-я подзадача эпика №1", "NEW");
        Subtask subtask3 = manager.createSubtask(epic1.getId(), "Подзадача 1-2", "Это 1-я подзадача эпика №2", "NEW");
        System.out.println();


    }
}
