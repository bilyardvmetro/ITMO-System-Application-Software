import util.Client;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Main {
    public static void main(String[] args) {
        try {
//            Client client = new Client(InetAddress.getByName("localhost"), 8000); для теста на локалхосте
            Client client = new Client(InetAddress.getByName("helios.cs.ifmo.ru"), 2801); // порт выбирается самостоятельно
            client.run();
        } catch (UnknownHostException e) {
            System.out.println("Хоста с таким именем не существует");
        }
    }
}
