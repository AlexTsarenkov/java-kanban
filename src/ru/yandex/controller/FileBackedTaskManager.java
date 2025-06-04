package ru.yandex.controller;

import ru.yandex.model.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static java.nio.file.StandardCopyOption.*;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final Path pathToFile;
    private final String fileName;
    private final int flagToDelete = -1;

    public FileBackedTaskManager(String fileName) {
        super();
        this.fileName = fileName;
        pathToFile = Paths.get(fileName);
    }

    public void loadFromFile(String fileName) throws IOException {
        Path path = Paths.get(fileName);
        if (Files.exists(path)) {
            try (BufferedReader reader = new BufferedReader(new FileReader(path.toString()))) {
                while (reader.ready()) {
                    String line = reader.readLine();
                    createTaskFromString(line);
                }
            }
        }
    }

    public void refreshFile() throws IOException {
        if (Files.exists(pathToFile)) {
            Files.delete(pathToFile);
            createFileIfNotExists(pathToFile);
        }
    }

    @Override
    public int addNewTask(Task task) {
        int taskId = super.addNewTask(task);
        saveChanges(List.of(taskId), OperationType.ADD);
        return taskId;

    }

    @Override
    public void removeTaskById(int id) {
        super.removeTaskById(id);
        Map.of(id, -1);
        saveChanges(Map.of(id, flagToDelete), OperationType.REMOVE);
    }

    @Override
    protected void removeEpicTask(int epicTaskId) {
        ArrayList<Integer> epicSubTasks = getEpicTaskById(epicTaskId).getSubTasks();
        super.removeEpicTask(epicTaskId);

        HashMap<Integer, Integer> valuesToRemove = new HashMap<>();
        valuesToRemove.put(epicTaskId, flagToDelete);
        for (Integer subTaskId : epicSubTasks) {
            valuesToRemove.put(subTaskId, flagToDelete);
        }
        saveChanges(valuesToRemove, OperationType.REMOVE);
    }

    @Override
    protected void removeSubTask(int subTaskId, boolean isDeletingEpicTasks) {
        int epicSubTaskId = getSubTaskById(subTaskId).getEpicTaskId();
        super.removeSubTask(subTaskId, isDeletingEpicTasks);
        if (!isDeletingEpicTasks) {
            saveChanges(Map.of(subTaskId, flagToDelete), OperationType.REMOVE);
            saveChanges(Map.of(epicSubTaskId, epicSubTaskId), OperationType.UPDATE);
        }
    }

    @Override
    public void removeAllTasks() {
        HashMap<Integer, Integer> mapToRemove = new HashMap<>();
        for (int taskId : tasks.keySet()) {
            mapToRemove.put(taskId, flagToDelete);
        }
        saveChanges(mapToRemove, OperationType.REMOVE);
    }

    @Override
    public void taskAddSubTask(int taskId, int subtaskId) {
        super.taskAddSubTask(taskId, subtaskId);
        saveChanges(Map.of(taskId, taskId, subtaskId, subtaskId), OperationType.UPDATE);
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        int taskId = task.getId();
        saveChanges(Map.of(taskId, taskId), OperationType.UPDATE);
    }

    private void saveChanges(List<Integer> taskIDs, OperationType operationType) {
        try {
            createFileIfNotExists(pathToFile);
            if (operationType == OperationType.ADD) {
                addTaskToFile(taskIDs);
            }
        } catch (IOException ex) {
            throw new ManagerSaveException(ex.getMessage());
        }
    }

    private void saveChanges(Map<Integer, Integer> oldIdToNewIDMap, OperationType operationType) {
        try {
            createFileIfNotExists(pathToFile);
            if (operationType == OperationType.UPDATE || operationType == OperationType.REMOVE) {
                changeTaskInFile(oldIdToNewIDMap);
            }
        } catch (IOException ex) {
            throw new ManagerSaveException(ex.getMessage());
        }
    }

    private void addTaskToFile(List<Integer> newIDs) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(pathToFile.toString(), true));
        for (Integer id : newIDs) {
            Task task = getAnyTaskById(id);
            writer.write(task.toString() + "\n");
        }
        writer.close();
    }

    private void changeTaskInFile(Map<Integer, Integer> oldIdToNewIDMap) throws IOException {
        File tempFile = File.createTempFile(fileName, ".tmp");
        Path pathToTmp = Paths.get(tempFile.getAbsolutePath());
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(pathToTmp.toString()));
             BufferedReader reader = new BufferedReader(new FileReader(pathToFile.toString()))) {

            while (reader.ready()) {
                String line = reader.readLine();
                Integer taskId = getIdFromTaskString(line);
                if (oldIdToNewIDMap.containsKey(taskId)) {
                    Integer newTaskId = oldIdToNewIDMap.get(taskId);
                    if (newTaskId != flagToDelete) {
                        String newTaskString = getAnyTaskById(newTaskId).toString();
                        writer.write(newTaskString + "\n");
                    }
                } else {
                    writer.write(line + "\n");
                }
            }
        }

        Files.copy(pathToTmp, pathToFile, REPLACE_EXISTING);
        tempFile.delete();
    }

    private void createFileIfNotExists(Path path) throws IOException {
        if (!Files.exists(path)) {
            Files.createFile(path);
        }
    }

    private Integer getIdFromTaskString(String taskString) {
        return Integer.parseInt(taskString.substring(taskString.indexOf("id:") + 3, taskString.indexOf("name") - 2));
    }

    private void createTaskFromString(String taskString) {
        String[] fields = taskString.split(";");

        int id = 0;
        int epicTaskId = 0;
        String name = "";
        String description = "";
        ArrayList<Integer> subIDs = new ArrayList<>();
        TaskType taskType = null;

        for (String field : fields) {
            String[] keyValue = field.split(":");
            switch (keyValue[0].trim()) {
                case "id":
                    id = Integer.parseInt(keyValue[1]);
                    break;
                case "name":
                    name = keyValue[1];
                    break;
                case "description":
                    description = keyValue[1];
                    break;
                case "subIDs":
                    String formated = keyValue[1].replace("[", "").replace("]", "");
                    String[] values = formated.split(", ");
                    for (String value : values) {
                        subIDs.add(Integer.parseInt(value));
                    }
                    break;
                case "type":
                    taskType = TaskType.valueOf(keyValue[1]);
                    break;
                case "epicId":
                    epicTaskId = Integer.parseInt(keyValue[1]);
                    break;
            }
        }

        switch (taskType) {
            case TASK:
                Task task = new Task(name, description, id);
                tasks.put(id, task);
                break;
            case SUB_TASK:
                SubTask subTask = new SubTask(id, epicTaskId, name, description);
                subTasks.put(id, subTask);
                break;
            case EPIC_TASK:
                EpicTask epicTask = new EpicTask(id, name, description, subIDs);
                epicTasks.put(id, epicTask);
                break;
        }
        if (id > currentTaskId) {
            currentTaskId = id;
        }

    }
}
