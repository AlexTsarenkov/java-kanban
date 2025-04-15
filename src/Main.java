import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        int taskId1 = taskManager.createNewTask("Task 1", "Test Task 1");
        int taskId2 = taskManager.createNewTask("Task 2", "Test Task 2");
        int taskId3 = taskManager.createNewTask("Task 3", "Test Task 3");
        int taskId4 = taskManager.createNewTask("Task 4", "Test Task 4");
        taskManager.printTasks(true, true);

        System.out.println("-----------------------------------------");
        taskManager.taskAddSubTask(taskId1, taskId2);
        taskManager.taskAddSubTask(taskId1, taskId3);
        taskManager.printTasks(true, true);
        System.out.println("-----------------------------------------");

        SubTask task2 = (SubTask) taskManager.getTaskById(taskId2);
        SubTask newTask2 = new SubTask(task2, task2.getEpicTaskId());
        newTask2.setStatus(TaskStatus.DONE);
        taskManager.updateTask(newTask2);
        taskManager.printTasks(true, true);
        System.out.println("-----------------------------------------");

        SubTask task3 = (SubTask) taskManager.getTaskById(taskId3);
        SubTask newTask3 = new SubTask(task3, task3.getEpicTaskId());
        newTask3.setStatus(TaskStatus.DONE);
        taskManager.updateTask(newTask3);
        taskManager.printTasks(true, true);
        System.out.println("-----------------------------------------");

        taskManager.removeTaskById(taskId3);
        taskManager.printTasks(true, true);
        System.out.println("-----------------------------------------");

        ArrayList<SubTask> subTasks = taskManager.getEpicSubTasks(taskId1);
        System.out.println(subTasks);
        System.out.println("-----------------------------------------");

        taskManager.removeTaskById(taskId1);
        taskManager.printTasks(true, true);
        System.out.println("-----------------------------------------");

        taskManager.removeAllTasks();
        taskManager.printTasks(true, true);
        System.out.println("-----------------------------------------");
    }
}
