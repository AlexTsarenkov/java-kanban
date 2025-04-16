import Controller.TaskManager;
import Model.*;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        System.out.println("Test 1: Create 4 tasks");
        int taskId1 = taskManager.addNewTask(new Task("Task1", "Task 1 for test", 0));
        int taskId2 = taskManager.addNewTask(new Task("Task2", "Task 2 for test", 0));
        int taskId3 = taskManager.addNewTask(new Task("Task3", "Task 3 for test", 0));
        int taskId4 = taskManager.addNewTask(new Task("Task4", "Task 4 for test", 0));
        printTasks(taskManager);

        System.out.println("Test 2: Task 2 and Task 3 now subtasks of Task 1");
        taskManager.taskAddSubTask(taskId1, taskId2);
        taskManager.taskAddSubTask(taskId1, taskId3);
        printTasks(taskManager);

        System.out.println("Test 3: Change status of Task 2 to DONE. EPIC will be IN_PROCESS");
        SubTask task2 = taskManager.getSubTaskById(taskId2);
        task2.setStatus(TaskStatus.DONE);
        taskManager.updateTask(task2);
        printTasks(taskManager);

        System.out.println("Test 4: Change status of Task 3 to DONE. EPIC will be DONE");
        SubTask task3 = taskManager.getSubTaskById(taskId3);
        task3.setStatus(TaskStatus.DONE);
        taskManager.updateTask(task3);
        printTasks(taskManager);

        System.out.println("Test 5: Remove Task 3. EPIC should have one subtask now");
        taskManager.removeTaskById(taskId3);
        printTasks(taskManager);

        System.out.println("Test 6: Remove Task 1. Task 2 will be removed as subtask of Task 1");
        taskManager.removeTaskById(taskId1);
        printTasks(taskManager);

        System.out.println("Test 7: Removing all tasks");
        taskManager.removeAll();
        printTasks(taskManager);

    }

    public static void printTasks(TaskManager taskManager) {
        ArrayList<Task> tasks = taskManager.getAllTasks();
        ArrayList<EpicTask> epicTasks = taskManager.getAllEpicTasks();
        ArrayList<SubTask> subTasks = taskManager.getAllSubTasks();
        System.out.println("Tasks: " + tasks);
        System.out.println("EpicTasks: " + epicTasks);
        System.out.println("SubTasks: " + subTasks);
        System.out.println("-----------------------------------------");
    }

}
