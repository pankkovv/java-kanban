package manager;

import model.*;
import java.util.Set;
import java.util.TreeSet;


import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    HistoryManager managerHistory = Managers.getDefaultHistory();
    List<Task> listTask = new ArrayList<>();
    List<Epic> listEpic = new ArrayList<>();
    List<Subtask> listSubtask = new ArrayList<>();
    Set<Task> tasksAfterSorted = new TreeSet<>();
    private int idTask = 0;
    private int idEpic = 100;
    private int idSubtask = 200;

    @Override
    public List<Task> getTask() {
        return listTask;
    }

    @Override
    public List<Epic> getEpic() {
        return listEpic;
    }

    @Override
    public List<Subtask> getSubtask() {
        return listSubtask;
    }

    @Override
    public List<Subtask> getListAllSubtask(int idSearch) {
        for (Epic epic : listEpic) {
            if (epic.getId() == idSearch) {
                return epic.getListOfSubtasks();
            } else {
                System.out.println("У данной epic-задачи отсутствуют подзадачи.");
            }
        }
        return null;
    }

    @Override
    public void removeTask() {
        for (Task task : listTask) {
            managerHistory.remove(task.getId());
        }
        listTask.clear();
        System.out.println("Все задачи удалены.");
    }

    @Override
    public void removeEpic() {
        for (Epic epic : listEpic) {
            for (Subtask subtask : epic.getListOfSubtasks()) {
                managerHistory.remove(subtask.getId());
                listSubtask.remove(subtask);
            }
            managerHistory.remove(epic.getId());
        }
        listEpic.clear();
        System.out.println("Все epic-задачи удалены.");
    }

    @Override
    public void removeSubtask() {
        for (Subtask subtask : listSubtask) {
            managerHistory.remove(subtask.getId());
            for (Epic epic : listEpic) {
                if (epic.getListOfSubtasks().contains(subtask)) {
                    epic.removeListOfSubtasks(subtask);
                }
            }
        }
        listSubtask.clear();
        System.out.println("Все subtask-задачи удалены.");
    }

    @Override
    public Task getTaskId(int idSearch) {
        for (Task task : listTask) {
            if (task.getId() == idSearch) {
                managerHistory.add(task);
                return task;
            }
        }
        return null;
    }

    @Override
    public Epic getEpicId(int idSearch) {
        for (Epic epic : listEpic) {
            if (epic.getId() == idSearch) {
                managerHistory.add(epic);
                return epic;
            }
        }
        return null;
    }

    @Override
    public Subtask getSubtaskId(int idSearch) {
        for (Subtask subtask : listSubtask) {
            if (subtask.getId() == idSearch) {
                managerHistory.add(subtask);
                return subtask;
            }
        }
        return null;
    }

    @Override
    public Task createTask(String title, String description, String status) {
        Task task = new Task();
        task.setTitle(title);
        task.setDescription(description);
        task.setStatus(status);
        task.setId(generatorId(TypeTask.TASK.toString()));
        task.setStartTime(LocalDateTime.now());
        if (task.getStatus().equals(String.valueOf(StatusOfTask.DONE))) {
            task.setDuration(Duration.between(task.getStartTime(), LocalDateTime.now()));
            task.setEndTime(task.getStartTime().plus(task.getDuration()));
        }
        listTask.add(task);
        return task;
    }

    @Override
    public Epic createEpic(String title, String description, String status) {
        Epic epic = new Epic();
        epic.setTitle(title);
        epic.setDescription(description);
        epic.setStatus(status);
        epic.setId(generatorId(TypeTask.EPIC.toString()));
        if (epic.getListOfSubtasks().size() != 0) {
            LocalDateTime startTime = LocalDateTime.of(2000, 1, 1, 0, 0, 0);
            for (Subtask subtask : epic.getListOfSubtasks()) {
                if (subtask.getStartTime().isAfter(startTime)) {
                    startTime = subtask.getStartTime();
                }
            }
            epic.setStartTime(startTime);
        } else {
            epic.setStartTime(null);
        }
        if (epic.getStatus().equals(String.valueOf(StatusOfTask.DONE))) {
            epic.setStartTime(LocalDateTime.now());
            epic.setDuration(Duration.between(epic.getStartTime(), LocalDateTime.now()));
            epic.setEndTime(epic.getStartTime().plus(epic.getDuration()));
        }
        listEpic.add(epic);
        return epic;
    }

    @Override
    public Subtask createSubtask(int idSearch, String title, String description, String status) {
        for (Epic epic : listEpic) {
            if (epic.getId() == idSearch) {
                Subtask subtask = new Subtask();
                subtask.setTitle(title);
                subtask.setDescription(description);
                subtask.setStatus(status);
                subtask.setId(generatorId(TypeTask.SUB.toString()));
                subtask.setIdEpic(idSearch);
                subtask.setStartTime(LocalDateTime.now());
                if (subtask.getStatus().equals(String.valueOf(StatusOfTask.DONE))) {
                    subtask.setDuration(Duration.between(subtask.getStartTime(), LocalDateTime.now()));
                    subtask.setEndTime(subtask.getStartTime().plus(subtask.getDuration()));
                }
                listSubtask.add(subtask);
                epic.setListOfSubtasks(subtask);
                generatorStatusEpic(idSearch);
                if (epic.getListOfSubtasks().size() != 0) {
                    LocalDateTime startTime = LocalDateTime.of(3000, 1, 1, 0, 0, 0);
                    for (Subtask sub : epic.getListOfSubtasks()) {
                        if (sub.getStartTime().isBefore(startTime)) {
                            startTime = sub.getStartTime();
                        }
                    }
                    epic.setStartTime(startTime);
                } else {
                    epic.setStartTime(null);
                }
                getEndTimeEpic(idSearch);
                return subtask;
            }
        }
        return null;
    }

    @Override
    public void updateTask(int idSearch, String title, String description, String status) {
        for (Task task : listTask) {
            if (task.getId() == idSearch) {
                task.setTitle(title);
                task.setDescription(description);
                task.setStatus(status);
                if (task.getStatus().equals(String.valueOf(StatusOfTask.DONE))) {
                    if(task.getStartTime() == null){
                        task.setStartTime(LocalDateTime.now());
                    }
                    task.setDuration(Duration.between(task.getStartTime(), LocalDateTime.now()));
                    task.setEndTime(task.getStartTime().plus(task.getDuration()));
                }
            }
        }
    }

    @Override
    public void updateEpic(int idSearch, String title, String description) {
        for (Epic epic : listEpic) {
            if (epic.getId() == idSearch) {
                epic.setTitle(title);
                epic.setDescription(description);
                generatorStatusEpic(idSearch);
                getEndTimeEpic(idSearch);
            }
        }
    }

    @Override
    public void updateSubtask(int idSearchEpic, int idSearch, String title, String description, String status) {
        for (Epic epic : listEpic) {
            if (epic.getId() == idSearchEpic) {
                for (Subtask subtask : epic.getListOfSubtasks()) {
                    if (subtask.getId() == idSearch) {
                        subtask.setTitle(title);
                        subtask.setDescription(description);
                        subtask.setStatus(status);
                        if (subtask.getStatus().equals(String.valueOf(StatusOfTask.DONE))) {
                            if(subtask.getStartTime() == null){
                                subtask.setStartTime(LocalDateTime.now());
                            }
                            subtask.setDuration(Duration.between(subtask.getStartTime(), LocalDateTime.now()));
                            subtask.setEndTime(subtask.getStartTime().plus(subtask.getDuration()));
                        }
                        generatorStatusEpic(idSearchEpic);
                        getEndTimeEpic(idSearchEpic);
                        return;
                    }
                }
            }
        }
    }

    @Override
    public void removeTaskId(int idSearch) {
        for (Task task : listTask) {
            if (task.getId() == idSearch) {
                managerHistory.remove(task.getId());
                listTask.remove(task);
                return;
            }
        }
    }

    @Override
    public void removeEpicId(int idSearch) {
        for (Epic epic : listEpic) {
            if (epic.getId() == idSearch) {
                if (epic.getListOfSubtasks().size() != 0) {
                    for (Subtask subtask : epic.getListOfSubtasks()) {
                        managerHistory.remove(subtask.getId());
                        listSubtask.remove(subtask);
                    }
                }
                managerHistory.remove(epic.getId());
                listEpic.remove(epic);
                return;
            }
        }
    }

    @Override
    public void removeSubtaskId(int idSearch) {
        for (Subtask subtask : listSubtask) {
            if (subtask.getId() == idSearch) {
                for (Epic epic : listEpic) {
                    epic.removeListOfSubtasks(subtask);
                }
                generatorStatusEpic(subtask.getIdEpic());
                getEndTimeEpic(subtask.getIdEpic());
                managerHistory.remove(subtask.getId());
                listSubtask.remove(subtask);
                return;
            }
        }
    }

    @Override
    public int generatorId(String typeTask) {
        int id = 0;

        switch (typeTask) {
            case "TASK":
                id = idTask;
                ++idTask;
                break;
            case "EPIC":
                id = idEpic;
                ++idEpic;
                break;
            case "SUB":
                id = idSubtask;
                ++idSubtask;
                break;
        }
        return id;
    }

    @Override
    public void generatorStatusEpic(int idSearch) {
        boolean conditionOne = false;
        int conditionTwo = 0;
        List<Integer> listTypeStatus = new ArrayList<>();

        for (Epic epic : listEpic) {
            if (epic.getId() == idSearch) {
                conditionOne = epic.getListOfSubtasks().size() == 0;

                for (Subtask subtask : epic.getListOfSubtasks()) {
                    if (subtask.getStatus().equals(String.valueOf(StatusOfTask.NEW))) {
                        listTypeStatus.add(1);
                    } else if (subtask.getStatus().equals(String.valueOf(StatusOfTask.DONE))) {
                        listTypeStatus.add(-1);
                    } else {
                        listTypeStatus.add(0);
                    }
                }

                for (Integer type : listTypeStatus) {
                    conditionTwo += type;
                }

                if (conditionOne || conditionTwo == listTypeStatus.size()) {
                    epic.setStatus(String.valueOf(StatusOfTask.NEW));
                } else if (conditionTwo == -listTypeStatus.size()) {
                    epic.setStatus(String.valueOf(StatusOfTask.DONE));
                } else {
                    epic.setStatus(String.valueOf(StatusOfTask.IN_PROGRESS));
                }
            }

        }
    }

    @Override
    public List<Task> getHistory() {
        return managerHistory.getHistory();
    }

    public void getEndTimeEpic(int idSearch) {
        LocalDateTime startTime = LocalDateTime.of(3000, 1, 1, 0, 0, 0);
        LocalDateTime endTime;
        Duration durationEpicMoment;
        List<Integer> listTypeStatus = new ArrayList<>();
        Integer conditionOne = 0;

        for (Epic epic : listEpic) {
            if (epic.getId() == idSearch) {
                for (Subtask subtask : epic.getListOfSubtasks()) {
                    if (subtask.getStartTime().isBefore(startTime)) {
                        startTime = subtask.getStartTime();
                    }
                    if (subtask.getStatus().equals(String.valueOf(StatusOfTask.DONE))) {
                        listTypeStatus.add(-1);
                        durationEpicMoment = Duration.between(subtask.getStartTime(), subtask.getEndTime());
                        if (epic.getDuration() == null) {
                            epic.setDuration(durationEpicMoment);
                        } else {
                            epic.setDuration(epic.getDuration().plus(durationEpicMoment));
                        }
                    } else {
                        listTypeStatus.add(0);
                    }
                }

                for (Integer type : listTypeStatus) {
                    conditionOne += type;
                }

                if (conditionOne == -listTypeStatus.size()) {
                    if (epic.getListOfSubtasks().size() != 0) {
                        epic.setEndTime(startTime.plus(epic.getDuration()));

                    }
                } else {
                    epic.setEndTime(null);
                }
            }
        }
    }

    public Set<Task> getPrioritizedTasks() {
        int countTaskInProcess = 0;

        try {

            if(tasksAfterSorted.size() !=0){
                tasksAfterSorted.clear();
            }
            tasksAfterSorted.addAll(listTask);
            tasksAfterSorted.addAll(listSubtask);


            for (Task task : tasksAfterSorted) {
                if (task.getStatus().equals(StatusOfTask.NEW.toString())) {
                    countTaskInProcess++;
                } else if (task.getStatus().equals(StatusOfTask.IN_PROGRESS.toString())) {
                    countTaskInProcess++;
                }
            }

            if (countTaskInProcess != 0 && countTaskInProcess != 1) {
                throw new ValidationTaskException("Нельзя выполнять сразу несколько задач.");
            }

            for(Task t : tasksAfterSorted) {
                System.out.println(t);
            }
            return tasksAfterSorted;

        } catch (ValidationTaskException e){
            System.out.println(e.getMessage());
        }
        return null;
    }

    public static class ValidationTaskException extends RuntimeException{
        public ValidationTaskException(){}
        public ValidationTaskException(final String message){
            super(message);
        }
    }
}

