package manager;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TypeTask;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTasksManager extends InMemoryTaskManager{

    private Path fileManager;
    public FileBackedTasksManager (){}

    public FileBackedTasksManager (String fileName){
        this.fileManager = Paths.get(fileName);
    }

    @Override
    public Task createTask(String title, String description, String status){
        Task task = super.createTask(title, description, status);
        save();
        return task;
    }

    @Override
    public Epic createEpic(String title, String description, String status){
        Epic epic = super.createEpic(title, description, status);
        save();
        return epic;
    }

    @Override
    public Subtask createSubtask(int idSearch, String title, String description, String status){
        Subtask subtask = super.createSubtask(idSearch, title, description, status);
        save();
        return subtask;
    }

    @Override
    public void updateTask(int idSearch, String title, String description, String status){
        super.updateTask(idSearch, title, description, status);
        save();
    }

    @Override
    public void updateEpic(int idSearch, String title, String description){
        super.updateEpic(idSearch, title, description);
        save();
    }

    @Override
    public void updateSubtask(int idSearchEpic, int idSearch, String title, String description, String status){
        super.updateSubtask(idSearchEpic, idSearch, title, description, status);
        save();
    }

    @Override
    public void removeTask(){
        super.removeTask();
        save();
    }

    @Override
    public void removeEpic(){
        super.removeEpic();
        save();
    }

    @Override
    public void removeSubtask(){
        super.removeSubtask();
        save();
    }

    @Override
    public void removeTaskId(int idSearch){
        super.removeTaskId(idSearch);
    }

    @Override
    public void removeEpicId(int idSearch){
       super.removeEpicId(idSearch);
       save();
    }

    @Override
    public void removeSubtaskId(int idSearch){
        super.removeSubtaskId(idSearch);
        save();
    }

    @Override
    public List<Task> getHistory(){
        List<Task> list = super.getHistory();
        save();
        return list;
    }

    public String taskToString(Task task){
        String type = TypeTask.TASK.toString();
        String formatWrite = String.format("%s,%s,%s,%s,%s,%s%n" , task.getId(), type, task.getTitle(), task.getStatus(), task.getDescription(), task.getTitle());
        return formatWrite;
    }
    public String epicToString(Epic epic){
        String type = TypeTask.EPIC.toString();
        String formatWrite = String.format("%s,%s,%s,%s,%s,%s%n" , epic.getId(), type, epic.getTitle(), epic.getStatus(), epic.getDescription(), epic.getTitle());
        return formatWrite;
    }
    public String subtaskToString(Subtask subtask){
        String type = TypeTask.SUB.toString();
        String formatWrite = String.format("%s,%s,%s,%s,%s,%s%n" , subtask.getId(), type, subtask.getTitle(), subtask.getStatus(), subtask.getDescription(), subtask.getIdEpic());
        return formatWrite;
    }

    public Task taskFromString(String value){
        String[] taskArray = value.split(",");
        Task task = createTask(taskArray[2], taskArray[4], taskArray[3]);
        return task;
    }

    public Epic epicFromString(String value){
        String[] epicArray = value.split(",");
        Epic epic = createEpic(epicArray[2], epicArray[4], epicArray[3]);
        return epic;
    }

    public Subtask subtaskFromString(String value){
        String[] subtaskArray = value.split(",");
        Subtask subtask = createSubtask(Integer.parseInt(subtaskArray[5]), subtaskArray[2], subtaskArray[4], subtaskArray[3]);
        return subtask;
    }

    public static String historyToString(HistoryManager manager){
        StringBuilder  formatWrite = new StringBuilder();
        for(int i = 0; i < manager.getHistory().size(); i++){
            Task task = manager.getHistory().get(i);
            formatWrite.append(Integer.toString(task.getId()) + ",");
            if(i == manager.getHistory().size()-1){
                formatWrite.append(Integer.toString(task.getId()));
            }
        }
        return formatWrite.toString();
    }

    public static List<Integer> historyFromString(String value){
        String[] historyArray = value.split(",");
        List<Integer> historyList = new ArrayList<>();
        for(String id : historyArray){
            historyList.add(Integer.parseInt(id));
        }
        return historyList;
    }

    public void save() {
        try (Writer fileWriter = new FileWriter(String.valueOf(fileManager))){
            String csvFormat = ("id,type,name,status,description,epic" + System.lineSeparator());
            fileWriter.write(csvFormat);
            for(Task task : super.getTask()){
                fileWriter.write(taskToString(task));
            }
            for(Epic epic : super.getEpic()){
                fileWriter.write(epicToString(epic));
            }
            for(Subtask subtask : super.getSubtask()){
                fileWriter.write(subtaskToString(subtask));
            }
            if(!managerHistory.getHistory().isEmpty()) {
                fileWriter.write(historyToString(super.managerHistory));
            }
            if(!Files.exists(Paths.get(String.valueOf(fileManager)))) {
                throw new ManagerSaveException("Файл отсутствует, данные не сохранены.");
            }
        } catch (IOException e){
            throw new ManagerSaveException(e.getMessage());
        }
    }

    public static FileBackedTasksManager loadFromFile(File file){
        FileBackedTasksManager backupManager = new FileBackedTasksManager();
        try{
            String backupData = Files.readString(Path.of(file.toURI()));
            String[] backupTask = backupData.split(System.lineSeparator());
            for(String task : backupTask){
                if(task.contains(String.valueOf(TypeTask.TASK))){
                    backupManager.taskFromString(task);
                } else if(task.contains(String.valueOf(TypeTask.EPIC))){
                    backupManager.epicFromString(task);
                } else if(task.contains(String.valueOf(TypeTask.SUB))){
                    backupManager.subtaskFromString(task);
                }
            }

            List<Integer> backupHistory = backupManager.historyFromString(backupTask[backupTask.length - 1]);
            for(Integer id : backupHistory){
               if(backupManager.getTaskId(id) != null){
                   backupManager.managerHistory.add(backupManager.getTaskId(id));
               } else if(backupManager.getEpicId(id) != null){
                   backupManager.managerHistory.add(backupManager.getEpicId(id));
               } else if(backupManager.getSubtaskId(id) != null){
                   backupManager.managerHistory.add(backupManager.getSubtaskId(id));
               }
            }
        } catch (IOException e){
            throw new ManagerSaveException(e.getMessage());
        }
        return backupManager;
    }

    public static class ManagerSaveException extends RuntimeException{
        public ManagerSaveException(){}
        public ManagerSaveException(final String message){
            super(message);
        }
    }

    public static void main(String[] args) throws ManagerSaveException {
        System.out.println("Загрузка данных в файл");
        FileBackedTasksManager managerTaskToFile = new FileBackedTasksManager("autosave.csv");
        Task task0 = managerTaskToFile.createTask("Создать задачу", "Реализация функции создания задачи", "NEW");
        Task task1 = managerTaskToFile.createTask("Создать еще одну задачу", "Реализация функции создания задачи вторая попытка", "NEW");
        Task task2 = managerTaskToFile.createTask("Создать задачу №2", "Реализация функции менеджера по созданию задачи", "NEW");
        Epic epic0 = managerTaskToFile.createEpic("Создание эпик-задачи", "Тест реализации функции создания", "NEW");
        Epic epic1 = managerTaskToFile.createEpic("Создание эпик-задачи №1", "Тест реализации функции создания", "NEW");
        Epic epic2 = managerTaskToFile.createEpic("Создание эпик-задачи №2", "Тест реализации функции создания", "NEW");
        Subtask subtask0 = managerTaskToFile.createSubtask(epic0.getId(), "Подзадача 1", "Это 1-я подзадача эпика №1", "DONE");
        Subtask subtask1 = managerTaskToFile.createSubtask(epic0.getId(), "Подзадача 2", "Это 2-я подзадача эпика №1", "DONE");
        Subtask subtask2 = managerTaskToFile.createSubtask(epic0.getId(), "Подзадача 3", "Это 3-я подзадача эпика №1", "NEW");
        Subtask subtask3 = managerTaskToFile.createSubtask(epic1.getId(), "Подзадача 1-2", "Это 1-я подзадача эпика №2", "NEW");

        managerTaskToFile.getSubtaskId(subtask0.getId());
        managerTaskToFile.getSubtaskId(subtask1.getId());
        managerTaskToFile.getSubtaskId(subtask2.getId());
        managerTaskToFile.getSubtaskId(subtask3.getId());
        managerTaskToFile.getEpicId(epic0.getId());
        managerTaskToFile.getEpicId(epic1.getId());
        managerTaskToFile.getEpicId(epic2.getId());
        managerTaskToFile.getEpicId(epic0.getId());
        managerTaskToFile.getHistory();

        System.out.println("Загрузка данных из файла");
        Path backupFile = Paths.get("autosave.csv");
        FileBackedTasksManager managerTaskFromFile = loadFromFile(backupFile.toFile());

        System.out.println(managerTaskFromFile.getTask());
        System.out.println(managerTaskFromFile.getEpic());
        System.out.println(managerTaskFromFile.getSubtask());
        System.out.println(managerTaskFromFile.getHistory());
    }


}
