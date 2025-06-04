package ru.yandex.model;

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

    public EpicTask(int id, String name, String description, ArrayList<Integer> subTasks) {
        super(name, description, id);
        this.subTasks = subTasks;
        taskType = TaskType.EPIC_TASK;
    }

    @Override
    public String toString() {
        return  String.format("type:%s; id:%d; name:%s; description:%s; subIDs:",
                taskType, id, name, description) +  subTasks.toString();
    }

    public ArrayList<Integer> getSubTasks() {
        return new ArrayList<Integer>(subTasks);
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
