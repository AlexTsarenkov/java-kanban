import java.util.Objects;

public class Task {
    protected final int id;
    protected final String name;
    protected final String description;
    protected TaskStatus status;
    protected TaskType taskType;

    public Task(String name, String description, int id) {
        this.name = name;
        this.description = description;
        this.id = id;
        taskType = TaskType.TASK;
        status = TaskStatus.NEW;
    }

    // Override SECTION
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return this.id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id) +
                Objects.hashCode(name) +
                Objects.hashCode(description) +
                Objects.hashCode(status);
    }

    @Override
    public String toString() {
        return "Task{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                ", taskType=" + taskType +
                '}';
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public TaskType getTaskType() {
        return taskType;
    }

}
