import Modules.Server;

import java.net.InetSocketAddress;

public class Main {
    public static void main(String[] args) {
        Server server = new Server(new InetSocketAddress("localhost", 8000));
//        server.run(args);
        server.run(new String[]{"collection.csv"});
    }
}
