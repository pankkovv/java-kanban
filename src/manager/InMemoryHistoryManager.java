package manager;

import model.Node;
import model.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    List<Task> listHistory = new ArrayList<>();
    Map<Integer, Node> mapHistory = new HashMap<>();
    CustomLinkedList<Task> linkedListHistory = new CustomLinkedList<>();


    @Override
    public void add(Task task){
        if(mapHistory.containsKey(task.getId())){
            linkedListHistory.removeNode(mapHistory.get(task.getId()));
            mapHistory.remove(task.getId());
        }
        linkedListHistory.linkLast(task);
        mapHistory.put(task.getId(), linkedListHistory.tail);
    }

    @Override
    public List<Task> getHistory(){
        if(linkedListHistory.getSize() != 0) {
            linkedListHistory.getTasks();
            for (int i = 0; i < listHistory.size(); i++){
                if (listHistory.size() < 10) {
                    return listHistory;
                } else {
                    listHistory.remove(0);
                }
            }
        } else {
            System.out.println("Вы не просматривали определенные задачи. История пуста.");
        }
        return List.of();
    }

    @Override
    public void remove(int id){
        if(mapHistory.containsKey(id)) {
            linkedListHistory.removeNode(mapHistory.get(id));
        }
    }


    class CustomLinkedList<Task> {
        private Node<Task> head;

        private Node<Task> tail;

        private int size = 0;

        void linkLast(Task task){
            // Реализуйте метод
            final Node<Task> oldTail = tail;
            final Node<Task> newNode= new Node<>(task, null ,oldTail);
            tail = newNode;

            if(oldTail == null)
                head = newNode;
            else
                oldTail.next = newNode;
            size++;
        }
        void getTasks(){
            for (Node<Task> x = head; x != null; x = x.next) {
                    listHistory.add((model.Task) x.data);
            }
        }

        void removeNode(Node<Task> x){
            final Task element = x.data;
            final Node<Task> next = x.next;
            final Node<Task> prev = x.prev;

            if (prev == null) {
                head = next;
            } else {
                prev.next = next;
                x.prev = null;
            }

            if (next == null) {
                tail = prev;
            } else {
                next.prev = prev;
                x.next = null;
            }

            x.data = null;
            size--;

        }

        int getSize(){
            return size;
        }

    }
}