package ru.yandex.controller;

import ru.yandex.model.EpicTask;
import ru.yandex.model.SubTask;
import ru.yandex.model.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

class InMemoryTaskManagerTest {
    Task task1;
    Task task2;
    Task task3;
    int task1id;
    int task2id;
    int task3id;
    InMemoryTaskManager taskManager;

    @BeforeEach
    void BeforeEach() {
        task1 = new Task("Test task 1", "Task 1 description", 123);
        task2 = new Task("Test task 2", "Task 2 description", 234);
        task3 = new Task("Test task 3", "Task 3 description", 567);
        taskManager = new InMemoryTaskManager();
        task1id = taskManager.addNewTask(task1);
        task2id = taskManager.addNewTask(task2);
        task3id = taskManager.addNewTask(task3);
    }

    @Test
    void tasksShouldBeEqualWhenIdIsTheSame() {
        Task testTask1 = new Task("Test task 1", "Task 1 description", 123);
        Task testTask2 = new Task("Different name", "Completely different description", 123);
        Assertions.assertTrue(testTask1.equals(testTask2));
    }

    @Test
    void taskSubclassesShouldBeEqualWhenIdIsTheSame() {
        EpicTask testEpic = new EpicTask(task1);
        SubTask testSub = new SubTask(task1);
        Assertions.assertTrue(testEpic.equals(testSub));
    }

    @Test
    void epicTaskCantBeAddedAsSubTask() {
        EpicTask testEpic1 = new EpicTask(task1);
        EpicTask testEpic2 = new EpicTask(task2);

        taskManager.taskAddSubTask(testEpic1.getId(), testEpic2.getId());

        Assertions.assertArrayEquals(new ArrayList<Integer>().toArray(), testEpic1.getSubTasks().toArray());
    }

    @Test
    void subTaskCantBeAddedAsEpicTask() {
        SubTask subTask = new SubTask(task1);
        taskManager.taskAddSubTask(subTask.getId(), subTask.getId());
        Assertions.assertNull(taskManager.getEpicTaskById(subTask.getId()));
    }

    @Test
    void taskManagerCanAddTask() {
        Task testTask1 = new Task("Test task 1", "Task 1 description", 0);
        int testTask1Id = taskManager.addNewTask(testTask1);
        Assertions.assertTrue(testTask1Id == taskManager.getTaskById(testTask1Id).getId());
    }

    @Test
    void taskManagerCanAddEpicTask() {
        taskManager.taskAddSubTask(task1id, task2id);
        Assertions.assertNotNull(taskManager.getEpicTaskById(task1id));
    }

    @Test
    void taskManagerCanAddSubTask() {
        taskManager.taskAddSubTask(task1id, task2id);
        Assertions.assertNotNull(taskManager.getSubTaskById(task2id));
    }

    @Test
    void taskNameIsConsistentAfterAddingToManager() {
        Task testTask1 = new Task("Test task 1", "Task 1 description", 0);
        int testTask1Id = taskManager.addNewTask(testTask1);
        Assertions.assertTrue(testTask1.getName().equals(taskManager.getTaskById(testTask1Id).getName()));
    }

    @Test
    void taskDescriptionIsConsistentAfterAddingToManager() {
        Task testTask1 = new Task("Test task 1", "Task 1 description", 0);
        int testTask1Id = taskManager.addNewTask(testTask1);
        Assertions.assertTrue(testTask1.getDescription().equals(taskManager.getTaskById(testTask1Id).getDescription()));
    }

}