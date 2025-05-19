package ru.yandex.controller;

import ru.yandex.model.EpicTask;
import ru.yandex.model.SubTask;
import ru.yandex.model.Task;
import ru.yandex.model.TaskType;

import java.util.ArrayList;

public interface TaskManager {
    int addNewTask(Task task);

    Task getAnyTaskById(int id);

    Task getTaskById(int id);

    EpicTask getEpicTaskById(int id);

    SubTask getSubTaskById(int id);

    TaskType getTaskTypeById(int id);

    void removeTaskById(int id);

    void updateTask(Task task);

    void taskAddSubTask(int taskId, int subtaskId);

    ArrayList<Task> getAllTasks();

    ArrayList<EpicTask> getAllEpicTasks();

    ArrayList<SubTask> getAllSubTasks();

    void removeAllTasks();

    void removeAllEpicTasks();

    void removeAllSubTasks();

    void removeAll();

    ArrayList<SubTask> getEpicSubTasks(int epicTaskId);
}
