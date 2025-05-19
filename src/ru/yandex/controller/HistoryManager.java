package ru.yandex.controller;

import ru.yandex.model.Task;

import java.util.List;

public interface HistoryManager {
    void add(Task task);

    List<Task> getHistory();

    void remove(int id);

    void removeAll();
}
