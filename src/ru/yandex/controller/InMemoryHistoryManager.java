package ru.yandex.controller;

import ru.yandex.model.HistoryList;
import ru.yandex.model.Task;
import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {
    private final HistoryList history;

    public InMemoryHistoryManager() {
        this.history = new HistoryList();
    }

    @Override
    public void add(Task task) {
        history.linkLast(task);
    }

    @Override
    public ArrayList<Task> getHistory() {
        return history.getTasks();
    }

    @Override
    public void remove(int id) {
        history.remove(id);
    }

    @Override
    public void removeAll() {
        history.clear();
    }
}
