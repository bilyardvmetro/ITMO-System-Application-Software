import Modules.DBProvider;
import Modules.Server;

import java.net.InetSocketAddress;

public class Main {
    public static void main(String[] args) {
        // для сервака
         DBProvider.establishConnection("jdbc:postgresql://pg:5432/studs", "s413041", "iqKjXPvPPWFfyhij");
        Server server = new Server(new InetSocketAddress(2801));
        server.run();

//       Строчки для теста на локалхосте. Для гелиуса достаточно указать только порт
//        DBProvider.establishConnection("jdbc:postgresql://localhost:9999/studs", "s413041", "iqKjXPvPPWFfyhij");
//        Server server = new Server(new InetSocketAddress("localhost", 8000));
//        server.run();

    }
}
