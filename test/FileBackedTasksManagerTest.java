import manager.FileBackedTasksManager;
import manager.HistoryManager;
import manager.InMemoryHistoryManager;
import model.Epic;
import model.Subtask;
import model.Task;
import model.TypeTask;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {


    //Со стандартным поведением.
    @Test
    void taskToStringTest() {
        Task task = manager.createTask("Задача", "Функция создания задачи", "NEW");
        String type = TypeTask.TASK.toString();
        String formatWrite = String.format("%s,%s,%s,%s,%s,%s%n", task.getId(), type, task.getTitle(), task.getStatus(), task.getDescription(), task.getTitle());
        String expectedString = "0,TASK,Задача,NEW,Функция создания задачи,Задача\r\n";

        assertEquals(expectedString, formatWrite, "Строки для записи неодинаковые.");
    }

    @Test
    void epicToStringTest() {
        Epic epic = manager.createEpic("Задача", "Функция создания задачи", "NEW");
        String type = TypeTask.EPIC.toString();
        String formatWrite = String.format("%s,%s,%s,%s,%s,%s%n", epic.getId(), type, epic.getTitle(), epic.getStatus(), epic.getDescription(), epic.getTitle());
        String expectedString = "100,EPIC,Задача,NEW,Функция создания задачи,Задача\r\n";

        assertEquals(expectedString, formatWrite, "Строки для записи неодинаковые.");
    }

    @Test
    void subToStringTest() {
        Epic epic = manager.createEpic("Задача", "Функция создания задачи", "NEW");
        Subtask subtask = manager.createSubtask(epic.getId(), "Задача", "Функция создания задачи", "NEW");
        String type = TypeTask.SUB.toString();
        String formatWrite = String.format("%s,%s,%s,%s,%s,%s%n", subtask.getId(), type, subtask.getTitle(), subtask.getStatus(), subtask.getDescription(), subtask.getIdEpic());
        String expectedString = "200,SUB,Задача,NEW,Функция создания задачи,100\r\n";

        assertEquals(expectedString, formatWrite, "Строки для записи неодинаковые.");
    }

    @Test
    void TaskFromStringTest() {
        String value = "1,TASK,Задача,NEW,Функция создания задачи,Задача";
        String[] taskArray = value.split(",");
        Task task = manager.createTask(taskArray[2], taskArray[4], taskArray[3]);
        Task expectedTask = manager.getTaskId(task.getId());

        assertNotNull(expectedTask, "Задача не возвращается.");
        assertEquals(expectedTask, task, "Неодинаковые задачи.");
    }

    @Test
    void EpicFromStringTest() {
        String value = "100,EPIC,Задача,NEW,Функция создания задачи,Задача";
        String[] taskArray = value.split(",");
        Epic task = manager.createEpic(taskArray[2], taskArray[4], taskArray[3]);
        Epic expectedTask = manager.getEpicId(task.getId());

        assertNotNull(expectedTask, "Задача не возвращается.");
        assertEquals(expectedTask, task, "Неодинаковые задачи.");
    }

    @Test
    void SubFromStringTest() {
        String value = "200,SUB,Задача,NEW,Функция создания задачи,100";
        String[] taskArray = value.split(",");
        Epic epic = manager.createEpic("Задача", "Функция создания задачи", "NEW");
        Subtask task = manager.createSubtask(Integer.parseInt(taskArray[5]), taskArray[2], taskArray[4], taskArray[3]);
        Subtask expectedTask = manager.getSubtaskId(task.getId());

        assertNotNull(expectedTask, "Задача не возвращается.");
        assertEquals(expectedTask, task, "Неодинаковые задачи.");
    }

    @Test
    void HistoryToStringTest() {
        HistoryManager historyManager = new InMemoryHistoryManager();
        StringBuilder formatWrite = new StringBuilder();
        Epic epic = manager.createEpic("Задача", "Функция создания задачи", "NEW");
        Subtask subtask = manager.createSubtask(epic.getId(), "Задача", "Функция создания задачи", "NEW");
        historyManager.add(epic);
        historyManager.add(subtask);
        String expectedString = "100,200";

        for (int i = 0; i < historyManager.getHistory().size(); i++) {
            Task task = historyManager.getHistory().get(i);
            formatWrite.append(Integer.toString(task.getId()) + ",");
        }
        formatWrite.deleteCharAt(formatWrite.length() - 1);

        assertNotNull(historyManager.getHistory(), "История пустая.");
        assertEquals(expectedString, formatWrite.toString(), "Неодинаковые строки.");
    }

    @Test
    void HistoryFromStringTest() {
        String value = "100,200";
        String[] historyArray = value.split(",");
        List<Integer> historyList = new ArrayList<>();
        for (String id : historyArray) {
            historyList.add(Integer.parseInt(id));
        }
        List<Integer> expectedList = List.of(100, 200);

        assertEquals(expectedList, historyList, "Форматирование прошло с ошибкой.");
    }

    @Test
    void SaveTest() {
        FileBackedTasksManager managerFile = new FileBackedTasksManager("test.csv");
        Task task0 = managerFile.createTask("Создать задачу", "Реализация функции создания задачи", "NEW");
        Task task1 = managerFile.createTask("Создать еще одну задачу", "Реализация функции создания задачи вторая попытка", "NEW");
        Epic epic0 = managerFile.createEpic("Создание эпик-задачи", "Тест реализации функции создания", "NEW");
        Epic epic1 = managerFile.createEpic("Создание эпик-задачи №1", "Тест реализации функции создания", "NEW");
        Subtask subtask0 = managerFile.createSubtask(epic0.getId(), "Подзадача 1", "Это 1-я подзадача эпика №1", "DONE");
        Subtask subtask1 = managerFile.createSubtask(epic0.getId(), "Подзадача 2", "Это 2-я подзадача эпика №1", "DONE");
        managerFile.getTaskId(task0.getId());
        managerFile.getEpicId(epic0.getId());
        managerFile.getSubtaskId(subtask0.getId());
        assertTrue(Files.exists(Paths.get("test")), "Файл не создан.");
    }

    @Test
    void LoadFromFileTest() {
        FileBackedTasksManager managerFile = new FileBackedTasksManager("test.csv");
        Task task0 = managerFile.createTask("Создать задачу", "Реализация функции создания задачи", "NEW");
        Task task1 = managerFile.createTask("Создать еще одну задачу", "Реализация функции создания задачи вторая попытка", "NEW");
        Epic epic0 = managerFile.createEpic("Создание эпик-задачи", "Тест реализации функции создания", "NEW");
        Epic epic1 = managerFile.createEpic("Создание эпик-задачи №1", "Тест реализации функции создания", "NEW");
        Subtask subtask0 = managerFile.createSubtask(epic0.getId(), "Подзадача 1", "Это 1-я подзадача эпика №1", "DONE");
        Subtask subtask1 = managerFile.createSubtask(epic0.getId(), "Подзадача 2", "Это 2-я подзадача эпика №1", "DONE");
        managerFile.getTaskId(task0.getId());
        managerFile.getEpicId(epic0.getId());
        managerFile.getSubtaskId(subtask0.getId());
        managerFile.getHistory();

        FileBackedTasksManager backupManager = new FileBackedTasksManager();
        Path backupFile = Paths.get("test.csv");

        assertNotNull(backupFile, "Файл не существует.");

        try {
            String backupData = Files.readString(Path.of(backupFile.toUri()));
            String[] backupTask = backupData.split(System.lineSeparator());
            for (String task : backupTask) {
                if (task.contains(String.valueOf(TypeTask.TASK))) {
                    backupManager.taskFromString(task);
                } else if (task.contains(String.valueOf(TypeTask.EPIC))) {
                    backupManager.epicFromString(task);
                } else if (task.contains(String.valueOf(TypeTask.SUB))) {
                    backupManager.subtaskFromString(task);
                }
            }

            List<Integer> backupHistory = backupManager.historyFromString(backupTask[backupTask.length - 1]);
            for (Integer id : backupHistory) {
                if (backupManager.getTaskId(id) != null) {
                    backupManager.managerHistory.add(backupManager.getTaskId(id));
                } else if (backupManager.getEpicId(id) != null) {
                    backupManager.managerHistory.add(backupManager.getEpicId(id));
                } else if (backupManager.getSubtaskId(id) != null) {
                    backupManager.managerHistory.add(backupManager.getSubtaskId(id));
                }
            }
        } catch (IOException e) {
            throw new FileBackedTasksManager.ManagerSaveException(e.getMessage());
        }

        assertEquals(managerFile.getTask().get(0).toString(), backupManager.getTask().get(0).toString(), "Данные сохранены с ошибкой.");
        assertEquals(managerFile.getEpic().get(0).toString(), backupManager.getEpic().get(0).toString(), "Данные сохранены с ошибкой.");
        assertEquals(managerFile.getSubtask().get(0).toString(), backupManager.getSubtask().get(0).toString(), "Данные сохранены с ошибкой.");
    }

    //Пустой список задач.
    @Test
    void SaveTestEmpty() {
        FileBackedTasksManager managerFile = new FileBackedTasksManager("test.csv");
        managerFile.getHistory();
        managerFile.save();
        assertTrue(Files.exists(Paths.get("test")), "Файл не создан.");
    }

    @Test
    void LoadFromFileTestEmpty() {
        FileBackedTasksManager managerFile = new FileBackedTasksManager("test.csv");
        managerFile.getHistory();
        managerFile.save();

        FileBackedTasksManager backupManager = new FileBackedTasksManager();
        Path backupFile = Paths.get("test.csv");

        assertNotNull(backupFile, "Файл не существует.");

        final NumberFormatException exception = assertThrows(NumberFormatException.class, new Executable() {
            @Override
            public void execute() throws IOException {
                String backupData = Files.readString(Path.of(backupFile.toUri()));
                String[] backupTask = backupData.split(System.lineSeparator());
                for (String task : backupTask) {
                    if (task.contains(String.valueOf(TypeTask.TASK))) {
                        backupManager.taskFromString(task);
                    } else if (task.contains(String.valueOf(TypeTask.EPIC))) {
                        backupManager.epicFromString(task);
                    } else if (task.contains(String.valueOf(TypeTask.SUB))) {
                        backupManager.subtaskFromString(task);
                    }
                }

                List<Integer> backupHistory = backupManager.historyFromString(backupTask[backupTask.length - 1]);
                for (Integer id : backupHistory) {
                    if (backupManager.getTaskId(id) != null) {
                        backupManager.managerHistory.add(backupManager.getTaskId(id));
                    } else if (backupManager.getEpicId(id) != null) {
                        backupManager.managerHistory.add(backupManager.getEpicId(id));
                    } else if (backupManager.getSubtaskId(id) != null) {
                        backupManager.managerHistory.add(backupManager.getSubtaskId(id));
                    }
                }
            }
        });

        assertEquals("For input string: \"id\"", exception.getMessage());
    }

    // Эпик без подзадач.
    @Test
    void SaveTestEpicWithoutSub() {
        FileBackedTasksManager managerFile = new FileBackedTasksManager("test.csv");
        Task task0 = managerFile.createTask("Создать задачу", "Реализация функции создания задачи", "NEW");
        Task task1 = managerFile.createTask("Создать еще одну задачу", "Реализация функции создания задачи вторая попытка", "NEW");
        Epic epic0 = managerFile.createEpic("Создание эпик-задачи", "Тест реализации функции создания", "NEW");
        Epic epic1 = managerFile.createEpic("Создание эпик-задачи №1", "Тест реализации функции создания", "NEW");
        managerFile.getTaskId(task0.getId());
        managerFile.getEpicId(epic0.getId());
        assertTrue(Files.exists(Paths.get("test")), "Файл не создан.");
    }

    @Test
    void LoadFromFileTestEpicWithoutSub() {
        FileBackedTasksManager managerFile = new FileBackedTasksManager("test.csv");
        Task task0 = managerFile.createTask("Создать задачу", "Реализация функции создания задачи", "NEW");
        Task task1 = managerFile.createTask("Создать еще одну задачу", "Реализация функции создания задачи вторая попытка", "NEW");
        Epic epic0 = managerFile.createEpic("Создание эпик-задачи", "Тест реализации функции создания", "NEW");
        Epic epic1 = managerFile.createEpic("Создание эпик-задачи №1", "Тест реализации функции создания", "NEW");
        managerFile.getTaskId(task0.getId());
        managerFile.getEpicId(epic0.getId());
        managerFile.getHistory();

        FileBackedTasksManager backupManager = new FileBackedTasksManager();
        Path backupFile = Paths.get("test.csv");

        assertNotNull(backupFile, "Файл не существует.");

        try {
            String backupData = Files.readString(Path.of(backupFile.toUri()));
            String[] backupTask = backupData.split(System.lineSeparator());
            for (String task : backupTask) {
                if (task.contains(String.valueOf(TypeTask.TASK))) {
                    backupManager.taskFromString(task);
                } else if (task.contains(String.valueOf(TypeTask.EPIC))) {
                    backupManager.epicFromString(task);
                } else if (task.contains(String.valueOf(TypeTask.SUB))) {
                    backupManager.subtaskFromString(task);
                }
            }

            List<Integer> backupHistory = backupManager.historyFromString(backupTask[backupTask.length - 1]);
            for (Integer id : backupHistory) {
                if (backupManager.getTaskId(id) != null) {
                    backupManager.managerHistory.add(backupManager.getTaskId(id));
                } else if (backupManager.getEpicId(id) != null) {
                    backupManager.managerHistory.add(backupManager.getEpicId(id));
                } else if (backupManager.getSubtaskId(id) != null) {
                    backupManager.managerHistory.add(backupManager.getSubtaskId(id));
                }
            }
        } catch (IOException e) {
            throw new FileBackedTasksManager.ManagerSaveException(e.getMessage());
        }

        assertEquals(managerFile.getTask().get(0).toString(), backupManager.getTask().get(0).toString(), "Данные сохранены с ошибкой.");
        assertEquals(managerFile.getEpic().get(0).toString(), backupManager.getEpic().get(0).toString(), "Данные сохранены с ошибкой.");
    }

    //Пустой список истории.
    @Test
    void SaveTestHistoryIsEmpty() {
        FileBackedTasksManager managerFile = new FileBackedTasksManager("test.csv");
        Task task0 = managerFile.createTask("Создать задачу", "Реализация функции создания задачи", "NEW");
        Task task1 = managerFile.createTask("Создать еще одну задачу", "Реализация функции создания задачи вторая попытка", "NEW");
        Epic epic0 = managerFile.createEpic("Создание эпик-задачи", "Тест реализации функции создания", "NEW");
        Epic epic1 = managerFile.createEpic("Создание эпик-задачи №1", "Тест реализации функции создания", "NEW");
        Subtask subtask0 = managerFile.createSubtask(epic0.getId(), "Подзадача 1", "Это 1-я подзадача эпика №1", "DONE");
        Subtask subtask1 = managerFile.createSubtask(epic0.getId(), "Подзадача 2", "Это 2-я подзадача эпика №1", "DONE");

        assertTrue(Files.exists(Paths.get("test")), "Файл не создан.");
        assertEquals(List.of(), managerFile.getHistory(), "Список истории не пуст.");
    }

    @Test
    void LoadFromFileTestHistoryIsEmpty() {
        FileBackedTasksManager managerFile = new FileBackedTasksManager("test.csv");
        Task task0 = managerFile.createTask("Создать задачу", "Реализация функции создания задачи", "NEW");
        Task task1 = managerFile.createTask("Создать еще одну задачу", "Реализация функции создания задачи вторая попытка", "NEW");
        Epic epic0 = managerFile.createEpic("Создание эпик-задачи", "Тест реализации функции создания", "NEW");
        Epic epic1 = managerFile.createEpic("Создание эпик-задачи №1", "Тест реализации функции создания", "NEW");
        Subtask subtask0 = managerFile.createSubtask(epic0.getId(), "Подзадача 1", "Это 1-я подзадача эпика №1", "DONE");
        Subtask subtask1 = managerFile.createSubtask(epic0.getId(), "Подзадача 2", "Это 2-я подзадача эпика №1", "DONE");

        FileBackedTasksManager backupManager = new FileBackedTasksManager();
        Path backupFile = Paths.get("test.csv");

        assertNotNull(backupFile, "Файл не существует.");

        final NumberFormatException exception = assertThrows(NumberFormatException.class, new Executable() {
            @Override
            public void execute() throws IOException {
                String backupData = Files.readString(Path.of(backupFile.toUri()));
                String[] backupTask = backupData.split(System.lineSeparator());
                for (String task : backupTask) {
                    if (task.contains(String.valueOf(TypeTask.TASK))) {
                        backupManager.taskFromString(task);
                    } else if (task.contains(String.valueOf(TypeTask.EPIC))) {
                        backupManager.epicFromString(task);
                    } else if (task.contains(String.valueOf(TypeTask.SUB))) {
                        backupManager.subtaskFromString(task);
                    }
                }

                List<Integer> backupHistory = backupManager.historyFromString(backupTask[backupTask.length - 1]);
                for (Integer id : backupHistory) {
                    if (backupManager.getTaskId(id) != null) {
                        backupManager.managerHistory.add(backupManager.getTaskId(id));
                    } else if (backupManager.getEpicId(id) != null) {
                        backupManager.managerHistory.add(backupManager.getEpicId(id));
                    } else if (backupManager.getSubtaskId(id) != null) {
                        backupManager.managerHistory.add(backupManager.getSubtaskId(id));
                    }
                }
            }
        });

        assertEquals("For input string: \"SUB\"", exception.getMessage());

    }
}