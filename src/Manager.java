import java.util.ArrayList;
import java.util.List;

public class Manager {
    List<Task> listTask = new ArrayList<>();
    List<Epic> listEpic = new ArrayList<>();
    List<Subtask> listSubtask = new ArrayList<>();

    private int idTask = 0;
    private int idEpic = 0;
    private int idSubtask = 0;

    public List<Task> getTask() {
        return listTask;
    }

    public List<Epic> getEpic() {
        return listEpic;
    }

    public List<Subtask> getSubtask() {
        return listSubtask;
    }

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

    public void removeTask(String typeTask) {
        switch (typeTask) {
            case "task":
                listTask.clear();
                break;
            case "epic":
                for (Epic epic : listEpic) {
                    for (Subtask subtask : epic.getListOfSubtasks()) {
                        listSubtask.remove(subtask);
                    }
                }
                listEpic.clear();
                break;
            case "subtask":
                for (Subtask subtask : listSubtask) {
                    for (Epic epic : listEpic) {
                        if (epic.getListOfSubtasks().contains(subtask)) {
                            epic.removeListOfSubtasks(subtask);
                        }
                    }
                }
                listSubtask.clear();
                break;
        }
        System.out.println("Все задачи удалены.");
    }

    public Task getTaskId(int idSearch) {
        for (Task task : listTask) {
            if (task.getId() == idSearch) {
                return task;
            }
        }
        return null;
    }

    public Epic getEpicId(int idSearch) {
        for (Epic epic : listEpic) {
            if (epic.getId() == idSearch) {
                return epic;
            }
        }
        return null;
    }

    public Subtask getSubtaskId(int idSearch) {
        for (Subtask subtask : listSubtask) {
            if (subtask.getId() == idSearch) {
                return subtask;
            }
        }
        return null;
    }

    public Task createTask(String title, String description, String status) {
        Task task = new Task();
        task.setTitle(title);
        task.setDescription(description);
        task.setStatus(status);
        task.setId(generatorId("task"));
        listTask.add(task);
        return task;
    }

    public Epic createEpic(String title, String description, String status) {
        Epic epic = new Epic();
        epic.setTitle(title);
        epic.setDescription(description);
        epic.setStatus(status);
        epic.setId(generatorId("epic"));
        listEpic.add(epic);
        return epic;
    }

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

    public void updateTask(int idSearch, String title, String description, String status) {
        for (Task task : listTask) {
            if (task.getId() == idSearch) {
                task.setTitle(title);
                task.setDescription(description);
                task.setStatus(status);
            }
        }
    }

    public void updateEpic(int idSearch, String title, String description) {
        for (Epic epic : listEpic) {
            if (epic.getId() == idSearch) {
                epic.setTitle(title);
                epic.setDescription(description);
                generatorStatusEpic(idSearch);
            }
        }
    }

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

    public void removeTaskId(String typeTask, int idSearch) {
        switch (typeTask) {
            case "task":
                for (Task task : listTask) {
                    if (task.getId() == idSearch) {
                        listTask.remove(task);
                        return;
                    }
                }
                break;
            case "epic":
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
                break;
            case "subtask":
                for (Subtask subtask : listSubtask) {
                    if (subtask.getId() == idSearch) {
                        for (Epic epic : listEpic) {
                            epic.removeListOfSubtasks(subtask);
                        }
                        listSubtask.remove(subtask);
                        return;
                    }
                }
                break;
        }
    }

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

    public void generatorStatusEpic(int idSearch) {
        boolean conditionOne = false;
        boolean conditionTwo = false;
        boolean conditionThree = false;

        for (Epic epic : listEpic) {
            if (epic.getId() == idSearch) {
                conditionOne = epic.getListOfSubtasks().size() == 0;
                for (Subtask subtask : epic.getListOfSubtasks()) {
                    conditionTwo = subtask.getStatus().equals("NEW");
                    conditionThree = subtask.getStatus().equals("DONE");
                }
            }

            if (conditionOne || conditionTwo) {
                if (epic.getId() == idSearch) {
                    epic.setStatus("NEW");
                }
            } else if (conditionThree) {
                if (epic.getId() == idSearch) {
                    epic.setStatus("DONE");
                }
            } else {
                if (epic.getId() == idSearch) {
                    epic.setStatus("IN_PROGRESS");
                }
            }
        }
    }
}
