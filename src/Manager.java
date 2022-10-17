import java.util.HashMap;

public class Manager {
    HashMap<Integer, Task> mapTask = new HashMap<>();
    HashMap<Integer, Epic> mapEpic = new HashMap<>();
    HashMap<Integer, Subtask> mapSubtask = new HashMap<>();

    private int idTask = 0;
    private int idEpic = 0;
    private int idSubtask = 0;

    public void getTask(String typeTask) {
        switch (typeTask) {
            case "task":
                for (int id : mapTask.keySet()) {
                    System.out.println(mapTask.get(id).toString());
                }
                break;
            case "epic":
                for (int id : mapEpic.keySet()) {
                    System.out.println(mapEpic.get(id).toString());
                }
                break;
            case "subtask":
                for (int id : mapSubtask.keySet()) {
                    System.out.println(mapSubtask.get(id).toString());
                }
                break;
        }
    }

    public void getListAllSubtask(int idSearch) {
        if ((mapEpic.containsKey(idSearch)) && (mapEpic.get(idSearch).getListOfSubtasks().size() != 0)) {
            System.out.println(mapEpic.get(idSearch).getListOfSubtasks().values());
        } else {
            System.out.println("У данной epic-задачи отсутствуют подзадачи.");
        }
    }

    public void removeTask(String typeTask) {
        switch (typeTask) {
            case "task":
                mapTask.clear();
                break;
            case "epic":
                mapEpic.clear();
                break;
            case "subtask":
                mapSubtask.clear();
                break;
        }
        System.out.println("Все задачи удалены.");
    }

    public void getTaskId(String typeTask, int idSearch) {
        switch (typeTask) {
            case "task":
                if (mapTask.containsKey(idSearch)) {
                    System.out.println(mapTask.get(idSearch).toString());
                } else {
                    System.out.println("Такого id задачи не существует.");
                }
                break;
            case "epic":
                if (mapEpic.containsKey(idSearch)) {
                    System.out.println(mapEpic.get(idSearch).toString());
                } else {
                    System.out.println("Такого id задачи не существует.");
                }
                break;
            case "subtask":
                if (mapSubtask.containsKey(idSearch)) {
                    System.out.println(mapSubtask.get(idSearch).toString());
                } else {
                    System.out.println("Такого id задачи не существует.");
                }
                break;
        }
    }

    public void createTask(String title, String description, String status) {
        Task obj = new Task();
        obj.setTitle(title);
        obj.setDescription(description);
        obj.setStatus(status);
        obj.setId(generatorId("task"));
        mapTask.put(obj.getId(), obj);
    }

    public void createEpic(String title, String description, String status) {
        Epic obj = new Epic();
        obj.setTitle(title);
        obj.setDescription(description);
        obj.setStatus(status);
        obj.setId(generatorId("epic"));
        mapEpic.put(obj.getId(), obj);
    }

    public void createSubtask(int idSearch, String title, String description, String status) {
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
        } else {
            System.out.println("Выбранной epic-задачи не существует.");
        }
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
                    mapEpic.get(idSearch).getListOfSubtasks().clear();
                    mapEpic.remove(idSearch);
                } else {
                    System.out.println("Такого id задачи не существует.");
                }
                break;
            case "subtask":
                if (mapSubtask.containsKey(idSearch)) {
                    mapEpic.get(mapSubtask.get(idSearch).getIdEpic()).getListOfSubtasks().remove(idSearch);
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
        boolean conditionOne = mapEpic.get(idSearch).listOfSubtasks.size() == 0;
        boolean conditionTwo = false;
        boolean conditionThree = false;

        for (int idSubtask : mapEpic.get(idSearch).listOfSubtasks.keySet()) {
            conditionTwo = mapEpic.get(idSearch).listOfSubtasks.get(idSubtask).status.equals("NEW");
        }

        for (int idSubtask : mapEpic.get(idSearch).listOfSubtasks.keySet()) {
            conditionThree = mapEpic.get(idSearch).listOfSubtasks.get(idSubtask).status.equals("DONE");
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
