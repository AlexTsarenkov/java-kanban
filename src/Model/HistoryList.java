package Model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class HistoryList {
    private final Map<Integer, Node> history;
    private Node lastNode;

    public Node getLastNode(){
        return lastNode;
    }

    public HistoryList() {
        history = new HashMap<>();
    }

    public void linkLast(Task task) {
        if (history.containsKey(task.getId())) {
            remove(task.getId());
        }

        Node newNode = new Node(task, lastNode, null);
        if (lastNode != null) {
            lastNode.setNextNode(newNode);
        }

        lastNode = newNode;

        history.put(task.getId(), newNode);
    }

    public void remove(int id) {
        Node nodeToRemove = history.get(id);

        if (nodeToRemove != null) {
            history.remove(id);
            Node prevNode = nodeToRemove.getPreviousNode();
            Node nextNode = nodeToRemove.getNextNode();

            if (prevNode != null) {
                prevNode.setNextNode(nextNode);
            }

            if (nextNode != null) {
                nextNode.setPreviousNode(prevNode);
            }

            if (lastNode.equals(nodeToRemove)) {
                lastNode = nodeToRemove.getPreviousNode();
            }
        }
    }

    public ArrayList<Task> getTasks() {
        ArrayList<Task> tasks = new ArrayList<>();
        Node node = lastNode;
        while (node != null) {
            tasks.add(node.getTask());
            node = node.getPreviousNode();
        }

        Collections.reverse(tasks);
        return tasks;
    }

    public void clear(){
        for (Node node : history.values()) {
            node.setPreviousNode(null);
            node.setNextNode(null);
        }

        lastNode = null;
        history.clear();
    }
}
