package Controller;

public class Managers {
    public static TaskManager getDefaultTaskManager() {
        return new InMemoryTaskManager();
    }

    public static TaskManager getTaskManagerUsingHistoryManager(HistoryManager historyManager) {
        return new InMemoryTaskManager(historyManager);
    }

    public static HistoryManager getDefaultHistoryManager() {
        return new InMemoryHistoryManager();
    }
}
