package model;

import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Subtask extends Task {
    private int idEpic;

    public Integer getIdEpic() {
        return idEpic;
    }

    public void setIdEpic(Integer idEpic) {
        this.idEpic = idEpic;
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
        return "{" + "title=" + title +
                ", idEpic=" + idEpic +
                ", description=" + description +
                ", id=" + id +
                ", status=" + status +
                ", startTime=" + start +
                ", endTime=" + end +
                "}";
    }

    public boolean equals(Subtask sub) {
        if (this == sub) return true;
        if (sub == null || getClass() != sub.getClass()) return false;

        Subtask subNew = (Subtask) sub;

        return  Objects.equals(title, subNew.title) && Objects.equals(description, subNew.description) && Objects.equals(status, subNew.status);
    }
    @Override
    public int hashCode() {
        int result = Objects.hash(title, description, id, idEpic);
        result = 31 * result + Integer.hashCode(idEpic);
        return result;
    }
}
