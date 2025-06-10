package ru.yandex.model;

public class SubTask extends Task {
    private int epicTaskId;

    @Override
    public String toString() {
        return String.format("type:%s; id:%d; name:%s; description:%s; epicId:%d",
                taskType, id, name, description, epicTaskId);
    }


    public SubTask(Task task) {
        super(task.name, task.description, task.id);
        taskType = TaskType.SUB_TASK;
    }

    public SubTask(SubTask subTask) {
        super(subTask.name, subTask.description, subTask.id);
        this.epicTaskId = subTask.epicTaskId;
        taskType = TaskType.SUB_TASK;
    }

    public SubTask(int id, int epicTaskId, String name, String description) {
        super(name, description, id);
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
