public class Main {

    public static void main(String[] args) {

        Manager manager = new Manager();


        System.out.println("Тест содания задач:");
        Task task0 = (Task) manager.createTask("Создать задачу", "Реализация функции создания задачи", "NEW");
        Task task1 = (Task) manager.createTask("Создать еще одну задачу", "Реализация функции создания задачи вторая попытка", "NEW");
        Task task2 = (Task) manager.createTask("Создать задачу №2", "Реализация функции менеджера по созданию задачи", "NEW");
        System.out.println(manager.getTask());

        System.out.println();
        System.out.println("Получение по id:");
        System.out.println(manager.getTaskId(task0.getId()));

        System.out.println();
        System.out.println("Удаление по id:");
        manager.removeTaskId("task", task1.getId());
        System.out.println(manager.getTask());

        System.out.println();
        System.out.println("Удаление всех задач:");
        System.out.println(manager.getTask());
//        manager.removeTask("task");
//        System.out.println(manager.getTask());

        System.out.println();
        System.out.println("Тест создания Epic-задачи:");
        Epic epic0 = (Epic) manager.createEpic("Создание эпик-задачи", "Тест реализации функции создания", "NEW");
        Epic epic1 = (Epic) manager.createEpic("Создание эпик-задачи №1", "Тест реализации функции создания", "NEW");
        Epic epic2 = (Epic) manager.createEpic("Создание эпик-задачи №2", "Тест реализации функции создания", "NEW");
        System.out.println(manager.getEpic());
        manager.getListAllSubtask(epic0.getId());

        System.out.println();
        System.out.println("Получение Epic-задачи по id:");
        System.out.println(manager.getEpicId(epic1.getId()));

        System.out.println();
        System.out.println("Удаление Epic-задачи по id:");
        manager.removeTaskId("epic", epic2.getId());
        System.out.println(manager.getEpic());

        System.out.println();
        System.out.println("Тест создания Subtask-задачи:");
        Subtask subtask0 = (Subtask) manager.createSubtask(epic0.getId(), "Подзадача 1", "Это 1-я подзадача эпика №1", "NEW");
        Subtask subtask1 = (Subtask) manager.createSubtask(epic0.getId(), "Подзадача 2", "Это 2-я подзадача эпика №1", "NEW");
        Subtask subtask2 = (Subtask) manager.createSubtask(epic1.getId(), "Подзадача 1-2", "Это 1-я подзадача эпика №2", "NEW");
        System.out.println(manager.getSubtask());

        System.out.println();
        System.out.println("Получение списка всех Subtask-задач определенного эпика:");
        System.out.println(manager.getListAllSubtask(epic0.getId()));

        System.out.println();
        System.out.println("Удаление Subtask-задачи:");
        System.out.println(manager.getSubtask());
        System.out.println();
        manager.removeTaskId("subtask", subtask2.getId());
        System.out.println(manager.getSubtask());
        System.out.println();
        System.out.println(manager.getEpic());

        System.out.println();
        System.out.println("Тест управления статусами epic-задачи:");
        manager.updateSubtask(epic0.getId(), subtask0.getId(), "Обновление подзадачи 1", "Это новая 1-я подзадача эпика №1", "DONE");
        manager.updateSubtask(epic0.getId(), subtask1.getId(), "Обновление подзадачи 2", "Это новая 2-я подзадача эпика №1", "DONE");
        System.out.println(manager.getSubtask());
        System.out.println();
        System.out.println(manager.getEpic());
        System.out.println();
        manager.createSubtask(epic1.getId(), "Новая подзадача 1 эпика №2", "Это новая задача, созданная после теста удаления", "IN_PROGRESS");
        System.out.println(manager.getEpic());

        System.out.println();
        System.out.println("Тест обновления задач:");
        manager.updateTask(task0.getId(), "Обновленная задача", "Тест функции обновления задачи", "IN_PROGRESS");
        manager.updateEpic(epic1.getId(), "Обновленная epic-задача", "Тест функции обновления epic-задачи");
        System.out.println(manager.getTask());
        System.out.println(manager.getEpic());

    }
}
