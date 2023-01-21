package manager;

import model.*;

import java.util.TreeSet;


import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    public HistoryManager managerHistory = Managers.getDefaultHistory();

    Comparator<Task> comparator = (o1, o2) -> {
        if (o1.getStartTime() == null && o2.getStartTime() == null) {
            return Integer.compare(1, 0);
        } else if (o1.getStartTime() != null && o2.getStartTime() == null) {
            return Integer.compare(0, 1);
        } else if (o1.getStartTime() == null && o2.getStartTime() != null) {
            return Integer.compare(1, 0);
        }

        if (o1.getStartTime().isAfter(o2.getStartTime())) {
            return 1;
        } else if (o1.getStartTime().isBefore(o2.getStartTime())) {
            return -1;
        }

        if (o1.equals(o2)) {
            return 0;
        } else {
            return 1;
        }
    };

    public List<Task> listTask = new ArrayList<>();
    public List<Epic> listEpic = new ArrayList<>();
    public List<Subtask> listSubtask = new ArrayList<>();
    public TreeSet<Task> setAfterSorted = new TreeSet<>(comparator);
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
        return List.of();
    }

    @Override
    public void removeTask() {
        for (Task task : listTask) {
            managerHistory.remove(task.getId());
            setAfterSorted.remove(task);
        }
        listTask.clear();
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
    }

    @Override
    public void removeSubtask() {
        for (Subtask subtask : listSubtask) {
            managerHistory.remove(subtask.getId());
            setAfterSorted.remove(subtask);
            for (Epic epic : listEpic) {
                if (epic.getListOfSubtasks().contains(subtask)) {
                    epic.removeListOfSubtasks(subtask);
                }
            }
        }
        listSubtask.clear();
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
        if (!(status.equals(String.valueOf(StatusOfTask.NEW)) || status.equals(String.valueOf(StatusOfTask.IN_PROGRESS)) || status.equals(String.valueOf(StatusOfTask.DONE)))) {
            throw new ValidationTaskException("Нельзя использовать статус:" + status);
        }
        task.setId(generatorId(TypeTask.TASK.toString()));
        task.setStartTime(LocalDateTime.now());
        if (task.getStatus().equals(String.valueOf(StatusOfTask.DONE))) {
            task.setDuration(Duration.between(task.getStartTime(), LocalDateTime.now()));
            task.setEndTime(task.getStartTime().plus(task.getDuration()));
        }
        for (Task oldTask : getPrioritizedTasks()) {
            if (oldTask.getEndTime() == null) {
                throw new ValidationTaskException("Нельзя выполнять сразу несколько задач.");
            } else if (task.getStartTime().isAfter(oldTask.getEndTime()) && task.getStartTime().isBefore(oldTask.getEndTime())) {
                throw new ValidationTaskException("Нельзя выполнять сразу несколько задач.");
            }
        }
        listTask.add(task);
        if (!setAfterSorted.contains(task)) {
            setAfterSorted.add(task);
        }
        return task;
    }

    @Override
    public Epic createEpic(String title, String description, String status) {
        Epic epic = new Epic();
        epic.setTitle(title);
        epic.setDescription(description);
        epic.setStatus(status);
        if (!(status.equals(String.valueOf(StatusOfTask.NEW)) || status.equals(String.valueOf(StatusOfTask.IN_PROGRESS)) || status.equals(String.valueOf(StatusOfTask.DONE)))) {
            throw new ValidationTaskException("Нельзя использовать статус:" + status);
        }
        epic.setId(generatorId(TypeTask.EPIC.toString()));
        if (epic.getStatus().equals(String.valueOf(StatusOfTask.DONE))) {
            if (!epic.getListOfSubtasks().isEmpty()) {
                LocalDateTime startTime = LocalDateTime.of(3000, 1, 1, 0, 0, 0);
                if (epic.getListOfSubtasks().size() != 0) {
                    for (Subtask subtask : epic.getListOfSubtasks()) {
                        if (subtask.getStartTime().isBefore(startTime)) {
                            startTime = subtask.getStartTime();
                        }
                    }
                    epic.setStartTime(startTime);
                    getEndTimeEpic(epic.getId());
                }
            } else {
                epic.setStartTime(LocalDateTime.now());
            }
            epic.setDuration(Duration.between(epic.getStartTime(), LocalDateTime.now()));
            epic.setEndTime(epic.getStartTime().plus(epic.getDuration()));
        } else {
            if (!epic.getListOfSubtasks().isEmpty()) {
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
                if (!(status.equals(String.valueOf(StatusOfTask.NEW)) || status.equals(String.valueOf(StatusOfTask.IN_PROGRESS)) || status.equals(String.valueOf(StatusOfTask.DONE)))) {
                    throw new ValidationTaskException("Нельзя использовать статус:" + status);
                }
                subtask.setId(generatorId(TypeTask.SUB.toString()));
                subtask.setIdEpic(idSearch);
                subtask.setStartTime(LocalDateTime.now());
                if (subtask.getStatus().equals(String.valueOf(StatusOfTask.DONE))) {
                    subtask.setDuration(Duration.between(subtask.getStartTime(), LocalDateTime.now()));
                    subtask.setEndTime(subtask.getStartTime().plus(subtask.getDuration()));
                }
                for (Task oldTask : getPrioritizedTasks()) {
                    if (oldTask.getEndTime() == null) {
                        throw new ValidationTaskException("Нельзя выполнять сразу несколько задач.");
                    } else if (subtask.getStartTime().isAfter(oldTask.getEndTime()) && subtask.getStartTime().isBefore(oldTask.getEndTime())) {
                        throw new ValidationTaskException("Нельзя выполнять сразу несколько задач.");
                    }
                }
                listSubtask.add(subtask);
                if (!setAfterSorted.contains(subtask)) {
                    setAfterSorted.add(subtask);
                }
                epic.setListOfSubtasks(subtask);
                generatorStatusEpic(idSearch);
                if (!epic.getListOfSubtasks().isEmpty()) {
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
                    if (task.getStartTime() == null) {
                        task.setStartTime(LocalDateTime.now());
                    }
                    task.setDuration(Duration.between(task.getStartTime(), LocalDateTime.now()));
                    task.setEndTime(task.getStartTime().plus(task.getDuration()));
                }
                for (Task oldTask : getPrioritizedTasks()) {
                    if (!task.equals(oldTask)) {
                        if (oldTask.getEndTime() == null) {
                            throw new ValidationTaskException("Нельзя выполнять сразу несколько задач.");
                        } else if (task.getStartTime().isAfter(oldTask.getEndTime()) && task.getStartTime().isBefore(oldTask.getEndTime())) {
                            throw new ValidationTaskException("Нельзя выполнять сразу несколько задач.");
                        }
                    }
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
                if (epic.getStatus().equals(String.valueOf(StatusOfTask.DONE))) {
                    LocalDateTime startTime = LocalDateTime.of(3000, 1, 1, 0, 0, 0);
                    Duration duration = Duration.ofSeconds(0, 0);
                    if (!epic.getListOfSubtasks().isEmpty()) {
                        for (Subtask subtask : epic.getListOfSubtasks()) {
                            if (subtask.getStartTime().isBefore(startTime)) {
                                startTime = subtask.getStartTime();
                            }
                        }
                        epic.setStartTime(startTime);
                        getEndTimeEpic(epic.getId());
                    }
                } else {
                    if (!epic.getListOfSubtasks().isEmpty()) {
                        LocalDateTime startTime = LocalDateTime.of(3000, 1, 1, 0, 0, 0);
                        for (Subtask subtask : epic.getListOfSubtasks()) {
                            if (subtask.getStartTime().isBefore(startTime)) {
                                startTime = subtask.getStartTime();
                            }
                        }
                        epic.setStartTime(startTime);
                    } else {
                        epic.setStartTime(null);
                    }
                }
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
                            if (subtask.getStartTime() == null) {
                                subtask.setStartTime(LocalDateTime.now());
                            }
                            subtask.setDuration(Duration.between(subtask.getStartTime(), LocalDateTime.now()));
                            subtask.setEndTime(subtask.getStartTime().plus(subtask.getDuration()));
                        }
                        for (Task oldTask : getPrioritizedTasks()) {
                            if (!subtask.equals(oldTask)) {
                                if (oldTask.getEndTime() == null) {
                                    throw new ValidationTaskException("Нельзя выполнять сразу несколько задач.");
                                } else if (subtask.getStartTime().isAfter(oldTask.getEndTime()) && subtask.getStartTime().isBefore(oldTask.getEndTime())) {
                                    throw new ValidationTaskException("Нельзя выполнять сразу несколько задач.");
                                }
                            }
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
                setAfterSorted.remove(task);
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
                if (!epic.getListOfSubtasks().isEmpty()) {
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
                setAfterSorted.remove(subtask);
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
                    if (!epic.getListOfSubtasks().isEmpty()) {
                        epic.setEndTime(startTime.plus(epic.getDuration()));
                    }
                } else {
                    epic.setEndTime(null);
                }
            }
        }
    }

    public List<Task> getPrioritizedTasks() {
        List<Task> listAfterSorted = new ArrayList<>();
        listAfterSorted.addAll(setAfterSorted);
        return listAfterSorted;
    }

    public static class ValidationTaskException extends RuntimeException {
        public ValidationTaskException(final String message) {
            super(message);
        }
    }
}

