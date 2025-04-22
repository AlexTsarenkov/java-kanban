package Controller;

import Model.*;

import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryTaskManager implements TaskManager {
    private int currentTaskId;
    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, EpicTask> epicTasks;
    private final HashMap<Integer, SubTask> subTasks;
    private final HistoryManager historyManager;

    public InMemoryTaskManager() {
        currentTaskId = 0;
        tasks = new HashMap<>();
        epicTasks = new HashMap<>();
        subTasks = new HashMap<>();
        historyManager = new InMemoryHistoryManager();
    }

    public InMemoryTaskManager(HistoryManager historyManager) {
        currentTaskId = 0;
        tasks = new HashMap<>();
        epicTasks = new HashMap<>();
        subTasks = new HashMap<>();
        this.historyManager = historyManager;
    }

    @Override
    public int addNewTask(Task task) {
        Task newTask = new Task(task, getNewTaskId());
        tasks.put(newTask.getId(), newTask);
        return newTask.getId();
    }

    @Override
    public Task getTaskById(int id) {
        if (tasks.containsKey(id)) {
            Task task = new Task(tasks.get(id));
            historyManager.add(task);
            return task;
        }
        return null;
    }

    @Override
    public EpicTask getEpicTaskById(int id) {
        if (epicTasks.containsKey(id)) {
            EpicTask newEpicTask = new EpicTask(epicTasks.get(id));
            historyManager.add(newEpicTask);
            return newEpicTask;
        }
        return null;
    }

    @Override
    public SubTask getSubTaskById(int id) {
        if (subTasks.containsKey(id)) {
            SubTask subTask = new SubTask(subTasks.get(id));
            historyManager.add(subTask);
            return subTask;
        }
        return null;
    }

    @Override
    public TaskType getTaskTypeById(int id) {
        if (tasks.containsKey(id)) {
            return tasks.get(id).getTaskType();
        }

        if (epicTasks.containsKey(id)) {
            return epicTasks.get(id).getTaskType();
        }

        if (subTasks.containsKey(id)) {
            return subTasks.get(id).getTaskType();
        }

        return null;
    }

    @Override
    public void removeTaskById(int id) {

        if (tasks.containsKey(id)) {
            tasks.remove(id);
        }

        if (epicTasks.containsKey(id)) {
            removeEpicTask(id);
        }

        //If it was SUBTASK need to recalc EPIC status
        if (subTasks.containsKey(id)) {
            removeSubTask(id);
        }
    }

    @Override
    public void updateTask(Task task) {
        if (task != null) {

            if (tasks.containsKey(task.getId())) {
                tasks.put(task.getId(), task);
            }

            if (epicTasks.containsKey(task.getId())) {
                EpicTask epicTask = epicTasks.get(task.getId());

                //Can't change EPIC status directly
                if (epicTask.getStatus() != task.getStatus()) {
                    return;
                }
                epicTasks.put(task.getId(), (EpicTask) task);

            }

            if (subTasks.containsKey(task.getId())) {
                SubTask oldTask = subTasks.get(task.getId());
                SubTask newTask = (SubTask) task;

                subTasks.put(newTask.getId(), newTask);

                //If status was changed and task a SUBTASK, need to recalc EPIC status
                if (oldTask.getStatus() != newTask.getStatus()) {
                    EpicTask epicTask = epicTasks.get(oldTask.getEpicTaskId());
                    calculateEpicTaskStatus(epicTask);
                }
            }
        }
    }

    @Override
    public void taskAddSubTask(int taskId, int subtaskId) {
        if (ifTaskIdExists(taskId) && ifTaskIdExists(subtaskId)) {
            EpicTask epicTask = null;
            SubTask subTask = null;

            //Convert target task to EPIC if necessary. SUB can't be converted to EPIC.
            switch (getTaskTypeById(taskId)) {
                case TASK:
                    epicTask = convertTaskToEpicTask(tasks.get(taskId));
                    break;
                case EPIC_TASK:
                    epicTask = epicTasks.get(taskId);
                    break;
                case SUB_TASK:
                    return;
            }

            //Convert add task to SUBTASK if necessary. EPIC can't be converted to SUB.
            switch (getTaskTypeById(subtaskId)) {
                case TASK:
                    subTask = convertTaskToSubTask(tasks.get(subtaskId));
                    break;
                case EPIC_TASK:
                    return;
                case SUB_TASK:
                    subTask = subTasks.get(subtaskId);
            }

            if (epicTask != null && subTask != null) {
                epicTask.addSubTask(subTask.getId());
                subTask.setEpicTaskId(epicTask.getId());
                calculateEpicTaskStatus(epicTask);
            }
        }
    }

    @Override
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<EpicTask> getAllEpicTasks() {
        return new ArrayList<>(epicTasks.values());
    }

    @Override
    public ArrayList<SubTask> getAllSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public void removeAllTasks() {
        if (!tasks.isEmpty()) {
            tasks.clear();
        }
    }

    @Override
    public void removeAllEpicTasks() {
        if (!epicTasks.isEmpty()) {
            for (EpicTask epicTask : epicTasks.values()) {
                removeEpicTask(epicTask.getId());
            }
        }
    }

    @Override
    public void removeAllSubTasks() {
        if (!subTasks.isEmpty()) {
            for (SubTask subTask : subTasks.values()) {
                removeSubTask(subTask.getId());
            }
        }
    }

    @Override
    public void removeAll() {
        removeAllTasks();
        removeAllEpicTasks();
        removeAllSubTasks();
    }

    @Override
    public ArrayList<SubTask> getEpicSubTasks(int epicTaskId) {
        ArrayList<SubTask> subTasks = new ArrayList<>();
        if (epicTasks.containsKey(epicTaskId)) {
            EpicTask epicTask = epicTasks.get(epicTaskId);
            ArrayList<Integer> epicSubTasks = epicTask.getSubTasks();

            for (Integer subTaskId : epicSubTasks) {
                SubTask subTask = new SubTask(subTasks.get(subTaskId));
                subTasks.add(subTask);
            }

        }
        return subTasks;
    }

    private boolean ifTaskIdExists(int taskId) {
        return tasks.containsKey(taskId) || epicTasks.containsKey(taskId) || subTasks.containsKey(taskId);
    }

    private void calculateEpicTaskStatus(EpicTask epicTask) {
        ArrayList<Integer> epicSubTasks = epicTask.getSubTasks();
        TaskStatus status = TaskStatus.IN_PROGRESS;

        if (!epicSubTasks.isEmpty()) {
            boolean allSubTasksDone = true;
            boolean allSubTasksNew = true;

            for (Integer subTaskId : epicSubTasks) {
                SubTask subTask = subTasks.get(subTaskId);

                if (subTask.getStatus() != TaskStatus.DONE) {
                    allSubTasksDone = false;
                }

                if (subTask.getStatus() != TaskStatus.NEW) {
                    allSubTasksNew = false;
                }
            }

            if (allSubTasksDone) {
                status = TaskStatus.DONE;
            }

            if (allSubTasksNew) {
                status = TaskStatus.NEW;
            }
        }

        epicTask.setStatus(status);
    }

    private void removeEpicTask(int epicTaskId) {
        //If it was EPIC need to remove all SUBTASKS
        EpicTask epicTask = epicTasks.get(epicTaskId);
        ArrayList<Integer> epicSubTasks = epicTask.getSubTasks();

        if (!epicSubTasks.isEmpty()) {
            for (Integer subTaskId : epicSubTasks) {
                subTasks.remove(subTaskId);
            }
        }
        epicTasks.remove(epicTaskId);
    }

    private void removeSubTask(int subTaskId) {
        SubTask subTask = subTasks.get(subTaskId);
        EpicTask epicTask = epicTasks.get(subTask.getEpicTaskId());
        epicTask.removeSubTask(subTaskId);
        subTasks.remove(subTaskId);
        calculateEpicTaskStatus(epicTask);
    }

    private int getNewTaskId() {
        currentTaskId++;
        return currentTaskId;
    }

    //Convert simple Model.Task to Epic
    private EpicTask convertTaskToEpicTask(Task task) {
        EpicTask epicTask = new EpicTask(task);
        tasks.remove(task.getId());
        epicTasks.put(task.getId(), epicTask);
        return epicTask;
    }

    //Convert simple Model.Task to Sub Model.Task
    private SubTask convertTaskToSubTask(Task task) {
        SubTask subTask = new SubTask(task);
        tasks.remove(subTask.getId());
        subTasks.put(task.getId(), subTask);
        return subTask;
    }

    public HistoryManager getHistoryManager(){
        return historyManager;
    }
}
