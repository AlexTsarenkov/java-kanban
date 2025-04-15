public class SubTask extends Task {
    private int epicTaskId;

    @Override
    public String toString() {
        return "SubTask{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                ", taskType=" + taskType +
                '}';
    }


    public SubTask(Task task) {
        super(task.name, task.description, task.id);
        taskType = TaskType.SUB_TASK;
    }

    public SubTask(Task task, int epicTaskId) {
        super(task.name, task.description, task.id);
        this.epicTaskId = epicTaskId;
        taskType = TaskType.SUB_TASK;
    }

    public Integer getEpicTaskId() {
        return epicTaskId;
    }

    public void setEpicTaskId(int epicTaskId) {
        this.epicTaskId = epicTaskId;
    }
}
