package Model;

import java.util.ArrayList;

public class EpicTask extends Task {
    private final ArrayList<Integer> subTasks;

    public EpicTask(Task task) {
        super(task.name, task.description, task.id);
        subTasks = new ArrayList<>();
        taskType = TaskType.EPIC_TASK;
    }

    public EpicTask(EpicTask epicTask) {
        super(epicTask.name, epicTask.description, epicTask.id);
        subTasks = epicTask.subTasks;
        taskType = TaskType.EPIC_TASK;
    }

    @Override
    public String toString() {
        return "Model.EpicTask{" +
                "subTasks=" + subTasks +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                ", taskType=" + taskType +
                '}';
    }

    public ArrayList<Integer> getSubTasks() {
        return subTasks;
    }

    public void addSubTask(Integer subTaskId) {
        if (!subTasks.contains(subTaskId)) {
            subTasks.add(subTaskId);
        }
    }

    public void removeSubTask(Integer subTaskId) {
        subTasks.remove(subTaskId);
    }
}
