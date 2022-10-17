public class Main {

    public static void main(String[] args) {

        Manager manager = new Manager();

        System.out.println("Тест содания задач:");
        manager.createTask("Создать задачу", "Реализация функции создания задачи", "NEW");
        manager.createTask("Создать еще одну задачу", "Реализация функции создания задачи вторая попытка", "NEW");
        manager.createTask("Создать задачу №3", "Реализация функции менеджера по созданию задачи", "NEW");
        manager.getTask("task");

        System.out.println();
        System.out.println("Получение по id:");
        manager.getTaskId("task", 1);

        System.out.println();
        System.out.println("Удаление по id:");
        manager.removeTaskId("task", 2);
        manager.getTask("task");

        System.out.println();
        System.out.println("Удаление всех задач:");
        manager.getTask("task");
//        manager.removeTask("task");
//        manager.getTask("task");

        System.out.println();
        System.out.println("Тест создания Epic-задачи:");
        manager.createEpic("Создание эпик-задачи", "Тест реализации функции создания", "NEW");
        manager.createEpic("Создание эпик-задачи №1", "Тест реализации функции создания", "NEW");
        manager.createEpic("Создание эпик-задачи №2", "Тест реализации функции создания", "NEW");
        manager.getTask("epic");
        manager.getListAllSubtask(0);

        System.out.println();
        System.out.println("Получение Epic-задачи по id:");
        manager.getTaskId("epic", 0);

        System.out.println();
        System.out.println("Удаление Epic-задачи по id:");
        manager.removeTaskId("epic", 0);
        manager.getTask("epic");

        System.out.println();
        System.out.println("Тест создания Subtask-задачи:");
        manager.createSubtask(1, "Подзадача 1", "Это 1-я подзадача эпика №1", "NEW");
        manager.createSubtask(1, "Подзадача 2", "Это 2-я подзадача эпика №1", "NEW");
        manager.createSubtask(2, "Подзадача 1", "Это 1-я подзадача эпика №2", "NEW");
        manager.getTask("epic");

        System.out.println();
        System.out.println("Получение списка всех Subtask-задач определенного эпика:");
        manager.getListAllSubtask(1);

        System.out.println();
        System.out.println("Удаление Subtask-задачи:");
        manager.getTask("subtask");
        System.out.println();
        manager.removeTaskId("subtask", 2);
        manager.getTask("subtask");
        System.out.println();
        manager.getTask("epic");

        System.out.println();
        System.out.println("Тест управления статусами epic-задачи:");
        manager.updateSubtask(1, 0, "Обновление подзадачи 1", "Это новая 1-я подзадача эпика №1", "DONE");
        manager.updateSubtask(1, 1, "Обновление подзадачи 2", "Это новая 2-я подзадача эпика №1", "DONE");
        manager.getTask("subtask");
        System.out.println();
        manager.getTask("epic");
        System.out.println();
        manager.createSubtask(2, "Новая подзадача 1 эпика №2", "Это новая задача, созданная после теста удаления", "IN_PROGRESS");
        manager.getTask("epic");

        System.out.println();
        System.out.println("Тест обновления задач:");
        manager.updateTask(0, "Обновленная задача", "Тест функции обновления задачи", "IN_PROGRESS");
        manager.updateEpic(2, "Обновленная epic-задача", "Тест функции обновления epic-задачи");
        manager.getTask("task");
        System.out.println();
        manager.getTask("epic");

    }
}
