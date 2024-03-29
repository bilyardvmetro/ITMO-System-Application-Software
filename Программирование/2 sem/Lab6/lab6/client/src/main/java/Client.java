import CollectionObject.Vehicle;
import Network.Request;
import Network.Response;
import util.PromptScan;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.*;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Stack;

public class Client {
    public static void main(String[] args) {

        try {
            InetAddress host = InetAddress.getByName("localhost");
            int port = 8000;
            SocketAddress address = new InetSocketAddress(host, port);
            SocketChannel channel = SocketChannel.open();
            channel.connect(address);

            PromptScan.setUserScanner(new Scanner(System.in));
            var scanner = PromptScan.getUserScanner();

            ObjectOutputStream serverWriter = new ObjectOutputStream(channel.socket().getOutputStream());
            ObjectInputStream serverReader = new ObjectInputStream(channel.socket().getInputStream());

            System.out.println("Это крутое консольное приложение запущенно специально для пацанов");

            while (true) {
                System.out.print("> ");
                try {

                    while (scanner.hasNext()) {
                        var command = "";
                        var arguments = "";
                        Serializable objArgument;
                        String[] input = (scanner.nextLine() + " ").trim().split(" ", 2);
                        if (input.length == 2) {
                            arguments = input[1].trim();
                        }
                        command = input[0].trim();

                        if (validate(command, arguments)){
                            Request request;
                            if (
                                command.equalsIgnoreCase("add") ||
                                command.equalsIgnoreCase("update")
                            ){
                                objArgument = createForm();
                                request = new Request(command, arguments, objArgument);
                            } if (command.equalsIgnoreCase("executeScript")) {
                                // это переделать таким образом, чтобы сканер возвращался юзеру
                                PromptScan.setUserScanner(new Scanner(arguments));
                                scanner = PromptScan.getUserScanner();
                            }
                            else {
                                request = new Request(command, arguments);
                            }

                            serverWriter.writeObject(request);
                            System.out.println("запрос успешно отправлен");

                            Response response = (Response) serverReader.readObject();
                            Stack<Vehicle> collection = response.getCollection();
                            System.out.println(response.getMessage());
                            if (!collection.isEmpty()){
                                for (Vehicle element : collection) {
                                    System.out.println(element);
                                }
                            }
                        }

                    }

                } catch (NoSuchElementException e) {
                    System.out.println("Остановка клиента через консоль");
                    System.out.println(e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
                    System.exit(1);
                } catch (ClassNotFoundException e) {
                    System.out.println("Объект поступивший в ответ от сервера не найден");
                }

            }

        } catch (UnknownHostException e) {
            System.out.println("Хоста с таким именем не существует");
        } catch (ConnectException e){
            System.out.println("Сервер недоступен в данный момент. Пожалуйста, повторите попытку позже");
        } catch (IOException e) {
            System.out.println("Ошибка ввода/вывода");
        }
    }

    private static Serializable createForm() {
        return;
    }

    private static boolean validate(String command, String arguments) {
        switch (command){
            case "help":
                if (){

                }
        }
    }
}
