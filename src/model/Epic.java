package model;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {
    private List<Subtask> listOfSubtasks = new ArrayList<>();

    public List<Subtask> getListOfSubtasks() {
        return listOfSubtasks;
    }

    public void setListOfSubtasks(Subtask subtask) {
        listOfSubtasks.add(subtask);
    }

    public void removeListOfSubtasks(Subtask subtask) {
        listOfSubtasks.remove(subtask);
    }

    @Override
    public String toString() {
        String start = "null";
        String end = "null";
        if(startTime != null){
            start = startTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd|HH:mm:ss"));
        }
        if(endTime != null){
            end = endTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd|HH:mm:ss"));
        }
        return "{" +
                "title=" + title +
                ", listOfSubtasks=" + getListOfSubtasks() +
                ", description=" + description +
                ", id=" + id +
                ", status=" + status +
                ", startTime=" + start +
                ", endTime=" + end +
                "}";
    }

    public boolean equals(Epic epic) {
        if (this == epic) return true;
        if (epic == null || getClass() != epic.getClass()) return false;

        Epic taskNew = (Epic) epic;

        return  Objects.equals(title, taskNew.title) && Objects.equals(description, taskNew.description) && Objects.equals(status, taskNew.status);
    }
    @Override
    public int hashCode() {
        int result = Objects.hash(title, description, id, listOfSubtasks);
        result = 31 * result + Objects.hashCode(listOfSubtasks);
        return result;
    }

}
