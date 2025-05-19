package Model;

import java.util.Objects;

public class Node {
    private Node previousNode;
    private Node nextNode;
    private Task task;


    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        Node node = (Node) o;
        return task.equals(node.task);
    }

    @Override
    public int hashCode() {
        return task.hashCode();
    }

    @Override
    public String toString() {
        return "Node{" +
                "task='" + task + '\'' +
                ", prevNode='" + ( previousNode != null ? previousNode.getTask() : null ) + '\'' +
                ", nextNode=" + ( nextNode != null ? nextNode.getTask() : null ) +
                '}';
    }

    public Node(Task task, Node previousNode, Node nextNode) {
        this.task = task;
        this.previousNode = previousNode;
        this.nextNode = nextNode;
    }

    public Node getPreviousNode() {
        return previousNode;
    }

    public Node getNextNode() {
        return nextNode;
    }

    public Task getTask() {
        return task;
    }

    public void setPreviousNode(Node previousNode) {
        this.previousNode = previousNode;
    }

    public void setNextNode(Node nextNode) {
        this.nextNode = nextNode;
    }
}
