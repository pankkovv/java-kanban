package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Objects;

public class Task implements Comparable<Task> {
    protected String title;
    protected String description;

    protected Integer id;
    protected String status;

    protected LocalDateTime startTime;

    protected Duration duration;

    protected LocalDateTime endTime;

    @Override
    public String toString() {
        String start = "null";
        String end = "null";
        if (startTime != null) {
            start = startTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd|HH:mm:ss"));
        }
        if (endTime != null) {
            end = endTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd|HH:mm:ss"));
        }
        return "{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status='" + status + '\'' + ", startTime='" + start + '\'' + ", endTime='" + end + '\'' +
                '}';
    }


        public boolean equals(Task task) {
        if (this == task) return true;
        if (task == null || getClass() != task.getClass()) return false;

        Task taskNew = (Task) task;

        return  id != taskNew.getId() && Objects.equals(title, taskNew.title) && Objects.equals(description, taskNew.description) && Objects.equals(status, taskNew.status);
    }
    @Override
    public int hashCode() {
        int result = Objects.hash(title, description, id);
        result = 31 * result + Integer.hashCode(id);
        return result;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public int compareTo(Task o) {
        if(this.getStartTime() == null && o.getStartTime() != null){
            return Integer.compare(o.getStartTime().getNano(), 0);
        } else if(this.getStartTime() != null && o.getStartTime() == null){
            return Integer.compare(0, this.getStartTime().getNano());
        } else if(this.getStartTime() == null && o.getStartTime() == null){
            return Integer.compare(0, 0);
        } else {
            return Integer.compare(o.getStartTime().getNano(), this.getStartTime().getNano());
        }
    }
}
