package ru.yandex.controller;

import ru.yandex.model.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InMemoryHistoryManagerTest {
    InMemoryHistoryManager historyManager;

    @BeforeEach
    void beforeEach() {
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    void taskCanBeAddedToHistory() {
        Task task1 = new Task("Test task 1", "Task 1 description", 123);
        historyManager.add(task1);
        Assertions.assertTrue(historyManager.getHistory().contains(task1));
    }

    @Test
    void taskIdIsUnchangedAfterAddingToHistory() {
        Task task1 = new Task("Test task 1", "Task 1 description", 123);
        historyManager.add(task1);
        Assertions.assertTrue(task1.equals(historyManager.getHistory().getFirst()));
    }

    @Test
    void taskNameIsUnchangedAfterAddingToHistory() {
        Task task1 = new Task("Test task 1", "Task 1 description", 123);
        historyManager.add(task1);
        Assertions.assertTrue(task1.getName().equals(
                historyManager.getHistory().getFirst().getName())
        );
    }

    @Test
    void taskDescriptionIsUnchangedAfterAddingToHistory() {
        Task task1 = new Task("Test task 1", "Task 1 description", 123);
        historyManager.add(task1);
        Assertions.assertTrue(task1.getDescription().equals(
                historyManager.getHistory().getFirst().getDescription())
        );
    }

    @Test
    void historyManagerWorksProperly() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager(historyManager);

        int taskId1 = taskManager.addNewTask(new Task("Task1", "Task 1 for test", 0));
        int taskId2 = taskManager.addNewTask(new Task("Task2", "Task 2 for test", 0));
        int taskId3 = taskManager.addNewTask(new Task("Task3", "Task 3 for test", 0));
        int taskId4 = taskManager.addNewTask(new Task("Task4", "Task 4 for test", 0));

        taskManager.taskAddSubTask(taskId3, taskId4);

        taskManager.getTaskById(taskId1); //1
        taskManager.getTaskById(taskId2); //2
        taskManager.getEpicTaskById(taskId3); //3
        taskManager.getSubTaskById(taskId4); //4
        taskManager.getTaskById(taskId2); //5
        taskManager.getTaskById(taskId2); //6
        taskManager.getTaskById(taskId1); //7
        taskManager.getTaskById(taskId1); //8
        taskManager.getTaskById(taskId2); //9
        taskManager.getTaskById(taskId1); //10
        taskManager.getTaskById(taskId2); //11
        taskManager.getEpicTaskById(taskId3); //12

        Integer[] expectedIds = {4,1,2,3};
        Integer[] result = new Integer[4];

        int i = 0;
        for(Task task: historyManager.getHistory()) {
            result[i] = task.getId();
            i++;
        }

        Assertions.assertArrayEquals(expectedIds, result);
    }
}