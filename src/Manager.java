import java.util.HashMap;

public class Manager {
    HashMap<Integer , Task> mapTask = new HashMap<>();
    HashMap<Integer, Epic> mapEpic = new HashMap<>();
    HashMap<Integer, Subtask> mapSubtask = new HashMap<>();

    private int idTask = 0;
    private int idEpic = 0;
    private int idSubtask = 0;

    public HashMap<Integer, Task> getTask() {
        return mapTask;
    }

    public HashMap<Integer, Epic> getEpic() {
        return mapEpic;
    }

    public HashMap<Integer, Subtask> getSubtask() {
        return mapSubtask;
    }

    public HashMap<Integer, Subtask> getListAllSubtask(int idSearch) {
        if ((mapEpic.containsKey(idSearch)) && (mapEpic.get(idSearch).getListOfSubtasks().size() != 0)) {
            return mapEpic.get(idSearch).getListOfSubtasks();
        } else {
            System.out.println("У данной epic-задачи отсутствуют подзадачи.");
        }
        return null;
    }

    public void removeTask(String typeTask) {
        switch (typeTask) {
            case "task":
                mapTask.clear();
                break;
            case "epic":
                for (int idSearch : mapEpic.keySet()) {
                    Epic obj = mapEpic.get(idSearch);
                    for (int idSubtask : obj.getListOfSubtasks().keySet())
                        mapSubtask.remove(obj.getListOfSubtasks().get(idSearch).getId());
                }
                mapEpic.clear();
                break;
            case "subtask":
                for (int idSearch : mapSubtask.keySet()) {
                    Subtask obj = mapSubtask.get(idSearch);
                    mapEpic.get(obj.getIdEpic()).clearListOfSubtasks();
                }
                mapSubtask.clear();
                break;
        }
        System.out.println("Все задачи удалены.");
    }

    public Object getTaskId(int idSearch) {
        Task obj = new Task();
        if (mapTask.containsKey(idSearch)) {
            obj = mapTask.get(idSearch);
        } else {
            System.out.println("Такого id задачи не существует.");
        }
        return obj;
    }

    public Object getEpicId(int idSearch) {
        Epic obj = new Epic();
        if (mapEpic.containsKey(idSearch)) {
            obj = mapEpic.get(idSearch);
        } else {
            System.out.println("Такого id задачи не существует.");
        }
        return obj;
    }

    public Object getSubtaskId(int idSearch) {
        Subtask obj = new Subtask();
        if (mapSubtask.containsKey(idSearch)) {
            obj = mapSubtask.get(idSearch);
        } else {
            System.out.println("Такого id задачи не существует.");
        }
        return obj;
    }

    public Object createTask(String title, String description, String status) {
        Task obj = new Task();
        obj.setTitle(title);
        obj.setDescription(description);
        obj.setStatus(status);
        obj.setId(generatorId("task"));
        mapTask.put(obj.getId(), obj);
        return mapTask.get(obj.getId());
    }

    public Object createEpic(String title, String description, String status) {
        Epic obj = new Epic();
        obj.setTitle(title);
        obj.setDescription(description);
        obj.setStatus(status);
        obj.setId(generatorId("epic"));
        mapEpic.put(obj.getId(), obj);
        return mapEpic.get(obj.getId());
    }

    public Object createSubtask(int idSearch, String title, String description, String status) {
        if (mapEpic.containsKey(idSearch)) {
            Subtask obj = new Subtask();
            obj.setTitle(title);
            obj.setDescription(description);
            obj.setStatus(status);
            obj.setId(generatorId("subtask"));
            obj.setIdEpic(idSearch);
            mapSubtask.put(obj.getId(), obj);
            mapEpic.get(idSearch).setListOfSubtasks(obj.getId(), obj);
            generatorStatusEpic(idSearch);
            return mapSubtask.get(obj.getId());
        } else {
            System.out.println("Выбранной epic-задачи не существует.");
        }
        return null;
    }

    public void updateTask(int idSearch, String title, String description, String status) {
        if (mapTask.containsKey(idSearch)) {
            Task obj = mapTask.get(idSearch);
            obj.setTitle(title);
            obj.setDescription(description);
            obj.setStatus(status);
        } else {
            System.out.println("Такого id задачи не существует.");
        }
    }

    public void updateEpic(int idSearch, String title, String description) {
        if (mapEpic.containsKey(idSearch)) {
            Epic obj = mapEpic.get(idSearch);
            obj.setTitle(title);
            obj.setDescription(description);
            generatorStatusEpic(idSearch);
        } else {
            System.out.println("Такого id задачи не существует.");
        }
    }

    public void updateSubtask(int idSearchEpic, int idSearch, String title, String description, String status) {
        if (mapEpic.containsKey(idSearchEpic)) {
            if (mapSubtask.containsKey(idSearch)) {
                Subtask obj = mapSubtask.get(idSearch);
                obj.setTitle(title);
                obj.setDescription(description);
                obj.setStatus(status);
                generatorStatusEpic(idSearchEpic);
                mapEpic.get(idSearchEpic).setListOfSubtasks(obj.getId(), obj);
            }
        } else {
            System.out.println("Такого id задачи не существует.");
        }
    }

    public void removeTaskId(String typeTask, int idSearch) {
        switch (typeTask) {
            case "task":
                if (mapTask.containsKey(idSearch)) {
                    mapTask.remove(idSearch);
                } else {
                    System.out.println("Такого id задачи не существует.");
                }
                break;
            case "epic":
                if (mapEpic.containsKey(idSearch)) {
                    for(int idSearchSubtask : mapEpic.get(idSearch).getListOfSubtasks().keySet()){
                        mapSubtask.remove(mapEpic.get(idSearch).getListOfSubtasks().get(idSearchSubtask).getId());
                    }
                    mapEpic.remove(idSearch);
                } else {
                    System.out.println("Такого id задачи не существует.");
                }
                break;
            case "subtask":
                if (mapSubtask.containsKey(idSearch)) {
                    mapEpic.get(mapSubtask.get(idSearch).getIdEpic()).removeListOfSubtasks(idSearch);
                    mapSubtask.remove(idSearch);
                } else {
                    System.out.println("Такого id задачи не существует.");
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
        boolean conditionOne = mapEpic.get(idSearch).getListOfSubtasks().size() == 0;
        boolean conditionTwo = false;
        boolean conditionThree = false;

        for (int idSubtask : mapEpic.get(idSearch).getListOfSubtasks().keySet()) {
            conditionTwo = mapEpic.get(idSearch).getListOfSubtasks().get(idSubtask).status.equals("NEW");
        }

        for (int idSubtask : mapEpic.get(idSearch).getListOfSubtasks().keySet()) {
            conditionThree = mapEpic.get(idSearch).getListOfSubtasks().get(idSubtask).status.equals("DONE");
        }

        if (conditionOne || conditionTwo) {
            mapEpic.get(idSearch).status = "NEW";
        } else if (conditionThree) {
            mapEpic.get(idSearch).status = "DONE";
        } else {
            mapEpic.get(idSearch).status = "IN_PROGRESS";
        }

    }
}
