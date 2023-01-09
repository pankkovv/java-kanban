package manager;

import api.HttpTaskManager;

import java.io.IOException;

public class Managers {

   public static TaskManager getDefault(){ return new InMemoryTaskManager();}
    public static TaskManager getHttpDefault(String url) throws IOException, InterruptedException { return new HttpTaskManager(url);}

   public static HistoryManager getDefaultHistory(){
       return new InMemoryHistoryManager();
   }
}
