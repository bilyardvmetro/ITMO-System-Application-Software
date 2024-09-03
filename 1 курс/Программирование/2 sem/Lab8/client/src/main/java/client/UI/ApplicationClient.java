package client.UI;

import Utils.Client;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class ApplicationClient {
    private static Client client;

    static {
        try {
            client = new Client(InetAddress.getByName("localhost"), 8000);
//            client = new Client(InetAddress.getByName("helios.cs.ifmo.ru"), 2801);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }



    public static Client getClient() {
        return client;
    }
}
