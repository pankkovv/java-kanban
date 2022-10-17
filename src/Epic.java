import java.util.HashMap;

public class Epic extends Task {
    HashMap<Integer, Subtask> listOfSubtasks = new HashMap<>();

    public HashMap<Integer, Subtask> getListOfSubtasks() {
        return listOfSubtasks;
    }

    public void setListOfSubtasks(Integer idSearch, Subtask subtask) {
        this.listOfSubtasks.put(idSearch, subtask);
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
