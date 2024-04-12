import Modules.Server;

import java.net.InetSocketAddress;

public class Main {
    public static void main(String[] args) {
        Server server = new Server(new InetSocketAddress(2801));
        server.run(args);

//        Server server = new Server(new InetSocketAddress("localhost", 8000));
//        server.run(new String[]{"collection.csv"});
//        Строчки для теста на локалхосте. Для гелиуса достаточно указать только порт
    }
}
