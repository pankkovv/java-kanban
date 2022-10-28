import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    List<Task> listHistory = new ArrayList<>();

    @Override
    public void add(Task task){
        listHistory.add(task);
    }

    @Override
    public List<Task> getHistory(){
        if(!listHistory.isEmpty()) {
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
}
