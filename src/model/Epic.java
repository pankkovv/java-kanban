package model;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private List<Subtask> listOfSubtasks = new ArrayList<>();

    public List<Subtask> getListOfSubtasks() {
        return listOfSubtasks;
    }

    public void setListOfSubtasks(Subtask subtask) {
        listOfSubtasks.add(subtask);
    }

    public void clearListOfSubtasks() {
        listOfSubtasks.clear();
    }

    public void removeListOfSubtasks(Subtask subtask) {
        listOfSubtasks.remove(subtask);
    }

    @Override
    public String toString() {
        return "{title='" + title + "', listOfSubtasks='" + listOfSubtasks.toString() +
                "', " + "description='" + description + '\'' +
                ", id=" + id +
                ", status='" + status + '\'' +
                '}';
    }
}
