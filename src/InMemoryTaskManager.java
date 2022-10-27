import java.util.ArrayList;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {

    HistoryManager managerHistory = Managers.getDefaultHistory();
    List<Task> listTask = new ArrayList<>();
    List<Epic> listEpic = new ArrayList<>();
    List<Subtask> listSubtask = new ArrayList<>();

    enum StatusOfTask {
        NEW,
        IN_PROGRESS,
        DONE
    }

    private int idTask = 0;
    private int idEpic = 0;
    private int idSubtask = 0;

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
            if (epic.id == idSearch) {
                return epic.getListOfSubtasks();
            } else {
                System.out.println("У данной epic-задачи отсутствуют подзадачи.");
            }
        }
        return null;
    }

    @Override
    public void removeTask() {
        listTask.clear();
        System.out.println("Все задачи удалены.");
    }

    @Override
    public void removeEpic() {
        for (Epic epic : listEpic) {
            for (Subtask subtask : epic.getListOfSubtasks()) {
                listSubtask.remove(subtask);
            }
        }
        listEpic.clear();
        System.out.println("Все epic-задачи удалены.");
    }

    @Override
    public void removeSubtask() {
        for (Subtask subtask : listSubtask) {
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
        task.setId(generatorId("task"));
        listTask.add(task);
        return task;
    }

    @Override
    public Epic createEpic(String title, String description, String status) {
        Epic epic = new Epic();
        epic.setTitle(title);
        epic.setDescription(description);
        epic.setStatus(status);
        epic.setId(generatorId("epic"));
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
                subtask.setId(generatorId("subtask"));
                subtask.setIdEpic(idSearch);
                listSubtask.add(subtask);
                epic.setListOfSubtasks(subtask);
                generatorStatusEpic(idSearch);
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
                        generatorStatusEpic(idSearchEpic);
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
                        listSubtask.remove(subtask);
                    }
                }
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
                listSubtask.remove(subtask);
                return;
            }
        }
    }

    @Override
    public int generatorId(String typeTask) {
        int id = 0;

        switch (typeTask) {
            case "task":
                id = idTask;
                ++idTask;
                break;
            case "epic":
                id = idEpic;
                ++idEpic;
                break;
            case "subtask":
                id = idSubtask;
                ++idSubtask;
                break;
        }
        return id;
    }

    @Override
    public void generatorStatusEpic(int idSearch) {
        boolean conditionOne = false;
        boolean conditionTwo = false;
        boolean conditionThree = false;

        for (Epic epic : listEpic) {
            if (epic.getId() == idSearch) {
                conditionOne = epic.getListOfSubtasks().size() == 0;
                for (Subtask subtask : epic.getListOfSubtasks()) {
                    conditionTwo = subtask.getStatus().equals(String.valueOf(StatusOfTask.NEW));
                    conditionThree = subtask.getStatus().equals(String.valueOf(StatusOfTask.DONE));
                }
            }

            if (conditionOne || conditionTwo) {
                if (epic.getId() == idSearch) {
                    epic.setStatus(String.valueOf(StatusOfTask.NEW));
                }
            } else if (conditionThree) {
                if (epic.getId() == idSearch) {
                    epic.setStatus(String.valueOf(StatusOfTask.DONE));
                }
            } else {
                if (epic.getId() == idSearch) {
                    epic.setStatus(String.valueOf(StatusOfTask.IN_PROGRESS));
                }
            }
        }
    }

    @Override
    public List<Task> getHistory(){
        return managerHistory.getHistory();
    }

}
