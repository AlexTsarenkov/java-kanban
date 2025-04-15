import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private int currentTaskId;
    private final HashMap<Integer, Task> tasks;

    public TaskManager() {
        currentTaskId = 0;
        tasks = new HashMap<>();
    }

    private int getNewTaskId() {
        currentTaskId++;
        return currentTaskId;
    }

    public int createNewTask(String name, String description) {
        Task task = new Task(name, description, getNewTaskId());
        tasks.put(task.getId(), task);
        return task.getId();
    }

    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public void removeTaskById(int id) {
        if (tasks.containsKey(id)) {
            Task task = tasks.get(id);

            tasks.remove(id);
            //If it was SUBTASK need to recalc EPIC status
            if (task.getTaskType() == TaskType.SUB_TASK) {
                SubTask subTask = (SubTask) task;
                EpicTask epicTask = (EpicTask) tasks.get(subTask.getEpicTaskId());
                epicTask.removeSubTask(task.getId());
                calculateEpicTaskStatus(epicTask);
            }

            //If it was EPIC need to remove all SUBTASKS
            if (task.getTaskType() == TaskType.EPIC_TASK) {
                EpicTask epicTask = (EpicTask) task;
                ArrayList<Integer> subTasks = epicTask.getSubTasks();

                for (Integer subTaskId : subTasks) {
                    tasks.remove(subTaskId);
                }
            }

        }
    }

    void updateTask(Task task) {
        if (task != null) {
            if (tasks.containsKey(task.getId())) {
                Task oldTask = tasks.get(task.getId());
                if (oldTask.getTaskType() == task.getTaskType()) {

                    //Can't change EPIC status directly
                    if (oldTask.getStatus() != task.getStatus() && task.getTaskType() == TaskType.EPIC_TASK) {
                        return;
                    }

                    tasks.put(task.getId(), task);

                    //If status was changed and task a SUBTASK, need to recalc EPIC status
                    if (oldTask.getStatus() != task.getStatus() && task.getTaskType() == TaskType.SUB_TASK) {
                        SubTask subTask = (SubTask) tasks.get(task.getId());
                        EpicTask epicTask = (EpicTask) tasks.get(subTask.getEpicTaskId());
                        calculateEpicTaskStatus(epicTask);
                    }
                }
            }
        }
    }

    public void taskAddSubTask(int taskId, int subtaskId) {
        if (tasks.containsKey(taskId) && tasks.containsKey(subtaskId)) {
            EpicTask epicTask = null;
            SubTask subTask = null;

            Task targetTask = tasks.get(taskId);
            Task addTask = tasks.get(subtaskId);

            //Convert target task to EPIC if necessary. SUB can't be converted to EPIC.
            switch (targetTask.getTaskType()) {
                case TASK:
                    epicTask = convertTaskToEpicTask(targetTask);
                    break;
                case EPIC_TASK:
                    epicTask = (EpicTask) targetTask;
                    break;
                case SUB_TASK:
                    return;
            }

            //Convert add task to SUBTASK if necessary. EPIC can't be converted to SUB.
            switch (addTask.getTaskType()) {
                case TASK:
                    subTask = convertTaskToSubTask(addTask);
                    break;
                case EPIC_TASK:
                    return;
                case SUB_TASK:
                    subTask = (SubTask) addTask;
            }

            if (epicTask != null && subTask != null) {
                epicTask.addSubTask(subTask.getId());
                subTask.setEpicTaskId(epicTask.getId());
                calculateEpicTaskStatus(epicTask);
            }
        }
    }

    public void printTasks(boolean includeEpicTask, boolean includeSubTask) {
        for (Task task : tasks.values()) {
            if (!includeEpicTask && task.getTaskType() == TaskType.EPIC_TASK) {
                continue;
            }

            if (!includeSubTask && task.getTaskType() == TaskType.SUB_TASK) {
                continue;
            }

            System.out.println(task);
        }
    }

    public void removeAllTasks() {
        if (!tasks.isEmpty()) {
            tasks.clear();
        }
    }

    //Convert simple Task to Epic
    public EpicTask convertTaskToEpicTask(Task task) {
        EpicTask epicTask = new EpicTask(task);
        tasks.put(task.getId(), epicTask);
        return epicTask;
    }

    //Convert simple Task to Sub Task
    public SubTask convertTaskToSubTask(Task task) {
        SubTask subTask = new SubTask(task);
        tasks.put(task.getId(), subTask);
        return subTask;
    }


    private void calculateEpicTaskStatus(EpicTask epicTask) {
        ArrayList<Integer> subTasks = epicTask.getSubTasks();
        TaskStatus status = TaskStatus.IN_PROGRESS;

        if (!subTasks.isEmpty()) {
            boolean allSubTasksDone = true;
            boolean allSubTasksNew = true;

            for (Integer subTaskId : subTasks) {
                SubTask subTask = (SubTask) tasks.get(subTaskId);

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

    public ArrayList<SubTask> getEpicSubTasks(int epicTaskId) {
        ArrayList<SubTask> subTasks = new ArrayList<>();
        if (tasks.containsKey(epicTaskId)) {
            Task task = tasks.get(epicTaskId);
            if (task.getTaskType() == TaskType.EPIC_TASK) {
                EpicTask epicTask = (EpicTask) task;
                ArrayList<Integer> epicSubTasks = epicTask.getSubTasks();

                for (Integer subTaskId : epicSubTasks) {
                    SubTask subTask = (SubTask) tasks.get(subTaskId);
                    subTasks.add(subTask);
                }
            }
        }
        return subTasks;
    }

}
