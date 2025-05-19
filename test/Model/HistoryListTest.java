package Model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HistoryListTest {

    @Test
    void checkInitialStateOfLastNodeIsNull(){
        HistoryList list = new HistoryList();

        assertNull(list.getLastNode());
    }

    @Test
    void checkLastNodeIsEqualToLastAdded(){
        HistoryList list = new HistoryList();
        Task task1 = new Task("Test task 1", "Task 1 description", 123);
        Task task2 = new Task("Test task 1", "Task 1 description", 321);

        list.linkLast(task1);
        list.linkLast(task2);

        assertEquals(task2, list.getLastNode().getTask());
    }

    @Test
    void checkDuplicatesHandle() {
        HistoryList list = new HistoryList();
        Task task1 = new Task("Test task 1", "Task 1 description", 123);
        Task task2 = new Task("Test task 1", "Task 1 description", 123);
        Task task3 = new Task("Test task 1", "Task 1 description", 123);
        Task task4 = new Task("Test task 1", "Task 1 description", 321);

        list.linkLast(task1);
        list.linkLast(task2);
        list.linkLast(task3);
        list.linkLast(task4);

        // 3 duplicates should be 1 record in total + 1 unique = 2
        assertTrue(list.getTasks().size() == 2);
    }
}