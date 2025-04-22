package Controller;

public class Managers {
    public TaskManager getDefaultTaskManager() {
        return new InMemoryTaskManager();
    }

    public TaskManager getTaskManagerUsingHistoryManager(HistoryManager historyManager) {
        return new InMemoryTaskManager(historyManager);
    }

    public HistoryManager getDefaultHistoryManager() {
        return new InMemoryHistoryManager();
    }
}
