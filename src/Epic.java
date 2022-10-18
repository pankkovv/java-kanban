import java.util.HashMap;

public class Epic extends Task {
    private HashMap<Integer, Subtask> listOfSubtasks = new HashMap<>();

    public HashMap<Integer, Subtask> getListOfSubtasks() {
        return listOfSubtasks;
    }

    public void setListOfSubtasks(int idSearch, Subtask obj) {
        listOfSubtasks.put(idSearch, obj);
    }

    public void clearListOfSubtasks() {
        listOfSubtasks.clear();
    }

    public void removeListOfSubtasks(int idSearch){
        listOfSubtasks.remove(idSearch);
    }

    @Override
    public String toString() {
        return title + " {" + listOfSubtasks.values() +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status='" + status + '\'' +
                '}';
    }
}
