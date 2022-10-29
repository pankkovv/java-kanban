package manager;

import manager.HistoryManager;
import manager.InMemoryHistoryManager;
import manager.InMemoryTaskManager;

public class Managers {

   public static TaskManager getDefault(){
       return new InMemoryTaskManager();
   }

   public static HistoryManager getDefaultHistory(){
       return new InMemoryHistoryManager();
   }
}
