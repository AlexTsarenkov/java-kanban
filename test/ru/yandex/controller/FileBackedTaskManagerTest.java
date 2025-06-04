package ru.yandex.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.model.EpicTask;
import ru.yandex.model.SubTask;
import ru.yandex.model.Task;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {
    File tmpFile = null;
    Path pathToTmp = null;
    FileBackedTaskManager taskManager = null;

    @BeforeEach
    void setUpFile() throws IOException {
        tmpFile = File.createTempFile("FileBackedTaskManagerTest", ".tmp");
        pathToTmp = Paths.get(tmpFile.getAbsolutePath());
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(pathToTmp.toString()));) {
            writer.write("type:EPIC_TASK; id:1; name:Task1; description:Task 1 for test; subIDs:[2, 3]\n");
            writer.write("type:SUB_TASK; id:2; name:Task2; description:Task 2 for test; epicId:1\n");
            writer.write("type:SUB_TASK; id:3; name:Task3; description:Task 3 for test; epicId:1\n");
            writer.write("type:TASK; id:4; name:Task4; description:Task 4 for test\n");
        }

        taskManager = new FileBackedTaskManager(pathToTmp.toString());
    }

    @AfterEach
    void tearDownFile() throws IOException {
        tmpFile.delete();
    }

    @Test
    void loadFromFileTasksAreAdded() throws IOException {
        taskManager.loadFromFile(tmpFile.getAbsolutePath());
        Task expectedTask = new Task("Task4", "Task4, description", 4);
        Task actualTask = taskManager.getTaskById(4);
        Assertions.assertEquals(expectedTask, actualTask);

        EpicTask expectedEpicTask =
                new EpicTask(1, "Task1", "Task 1 for test", new ArrayList<Integer>(List.of(2, 3)));
        EpicTask actualEpicTask = taskManager.getEpicTaskById(1);
        Assertions.assertEquals(expectedEpicTask, actualEpicTask);

        SubTask expectedSubTask = new SubTask(2, 1, "Task2", "Task 2 for test");
        SubTask actualSubTask = taskManager.getSubTaskById(2);
        Assertions.assertEquals(expectedSubTask, actualSubTask);
    }

    @Test
    void addNewTask() throws IOException {
        boolean lineExist = false;
        int id = taskManager.addNewTask(new Task("Task1", "Test", 0));
        String expectedFileLine = String.format("type:TASK; id:4; name:Task4; description:Task 4 for test", id);
        try (BufferedReader reader = new BufferedReader(new FileReader(pathToTmp.toString()))) {
            while (reader.ready()) {
                String line = reader.readLine();
                if (line.equals(expectedFileLine)) {
                    lineExist = true;
                }
            }
        }

        Assertions.assertTrue(lineExist);
    }

    @Test
    void removeTaskById() throws IOException {
        boolean lineExist = false;
        taskManager.loadFromFile(tmpFile.getAbsolutePath());
        String expectedFileLine = "type:TASK; id:4; name:Task4; description:Task 4 for test";
        taskManager.removeTaskById(4);
        try (BufferedReader reader = new BufferedReader(new FileReader(pathToTmp.toString()))) {
            while (reader.ready()) {
                String line = reader.readLine();
                if (line.equals(expectedFileLine)) {
                    lineExist = true;
                }
            }
        }

        Assertions.assertFalse(lineExist);
    }

    @Test
    void removeEpicTask() throws IOException {
        boolean lineExist = false;
        boolean isSubDeleted = true;

        taskManager.loadFromFile(tmpFile.getAbsolutePath());
        String expectedFileLine = "type:EPIC_TASK; id:1; name:Task1; description:Task 1 for test; subIDs:[2, 3]";
        String sub1 = "type:SUB_TASK; id:2; name:Task2; description:Task 2 for test; epicId:1";
        String sub2 = "type:SUB_TASK; id:3; name:Task3; description:Task 3 for test; epicId:1";

        taskManager.removeEpicTask(1);

        try (BufferedReader reader = new BufferedReader(new FileReader(pathToTmp.toString()))) {
            while (reader.ready()) {
                String line = reader.readLine();
                if (line.equals(expectedFileLine)) {
                    lineExist = true;
                }
                if (line.equals(sub1) || line.equals(sub2)) {
                    isSubDeleted = false;
                }
            }
        }
        Assertions.assertFalse(lineExist);
        Assertions.assertTrue(isSubDeleted);
    }

    @Test
    void removeSubTask() throws IOException {
        boolean lineExist = false;
        boolean isEpicCorrect = false;
        taskManager.loadFromFile(tmpFile.getAbsolutePath());
        String expectedEpicLine = "type:EPIC_TASK; id:1; name:Task1; description:Task 1 for test; subIDs:[3]";
        String deletedSub = "type:SUB_TASK; id:2; name:Task2; description:Task 2 for test; epicId:1";

        taskManager.removeSubTask(2, false);

        try (BufferedReader reader = new BufferedReader(new FileReader(pathToTmp.toString()))) {
            while (reader.ready()) {
                String line = reader.readLine();
                if (line.equals(deletedSub)) {
                    lineExist = true;
                }
                if (line.equals(expectedEpicLine)) {
                    isEpicCorrect = true;
                }
            }
        }
        Assertions.assertFalse(lineExist);
        Assertions.assertTrue(isEpicCorrect);
    }

    @Test
    void taskAddSubTask() throws IOException {
        boolean lineExist = false;
        boolean isSubCorrect = false;
        taskManager.loadFromFile(tmpFile.getAbsolutePath());
        String expectedFileLine = "type:EPIC_TASK; id:1; name:Task1; description:Task 1 for test; subIDs:[2, 3, 4]";
        String expectedSub = "type:SUB_TASK; id:4; name:Task4; description:Task 4 for test; epicId:1";

        taskManager.taskAddSubTask(1, 4);

        try (BufferedReader reader = new BufferedReader(new FileReader(pathToTmp.toString()))) {
            while (reader.ready()) {
                String line = reader.readLine();
                if (line.equals(expectedFileLine)) {
                    lineExist = true;
                }
                if (line.equals(expectedSub)) {
                    isSubCorrect = true;
                }
            }
        }
        Assertions.assertTrue(lineExist);
        Assertions.assertTrue(isSubCorrect);
    }
}