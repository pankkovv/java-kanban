import api.HttpTaskServer;
import api.KVServer;
import api.KVTaskClient;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        HttpTaskServer api = new HttpTaskServer();
        api.start();

    }
}
