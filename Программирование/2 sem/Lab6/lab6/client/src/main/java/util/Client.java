package util;

import CollectionObject.VehicleModel;
import Exceptions.ScriptRecursionException;
import Network.Request;
import Network.Response;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Client {
    private Set<Path> scriptsNames = new TreeSet<>();
    private ObjectOutputStream serverWriter;
    private ObjectInputStream serverReader;
    private InetAddress host;
    private int port;

    public Client(InetAddress host, int port) {
        this.host = host;
        this.port = port;
    }

    public void run(){
        try {
            SocketAddress address = new InetSocketAddress(host, port);
            SocketChannel channel = SocketChannel.open();
            channel.connect(address);

            PromptScan.setUserScanner(new Scanner(System.in));
            var scanner = PromptScan.getUserScanner();

            serverWriter = new ObjectOutputStream(channel.socket().getOutputStream());
            serverReader = new ObjectInputStream(channel.socket().getInputStream());

            System.out.println("Это крутое консольное приложение запущенно специально для пацанов");

            while (true) {
                System.out.print("> ");
                try {

                    while (scanner.hasNext()) {
                        var command = "";
                        var arguments = "";
                        String[] input = (scanner.nextLine() + " ").trim().split(" ", 2);
                        if (input.length == 2) {
                            arguments = input[1].trim();
                        }
                        command = input[0].trim();

                        processUserPrompt(command, arguments);
                    }

                } catch (NoSuchElementException e) {
                    System.out.println("Остановка клиента через консоль");
                    System.out.println(e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
                    System.exit(1);
                } catch (ClassNotFoundException e) {
                    System.out.println("Объект поступивший в ответ от сервера не найден");
                }
            }

        } catch (ConnectException e){
            System.out.println("Сервер недоступен в данный момент. Пожалуйста, повторите попытку позже");
        } catch (IOException e) {
            System.out.println("Ошибка ввода/вывода");
            e.printStackTrace();
        }
    }

    private void processUserPrompt(String command, String arguments) throws IOException, ClassNotFoundException {
        Request request = null;
        if (
            command.equalsIgnoreCase("add") ||
            command.equalsIgnoreCase("update")
        ){
            VehicleModel objArgument = VehicleAsker.createElement();
            request = new Request(command, arguments, objArgument);
        }
        if (command.equalsIgnoreCase("exit")){
            System.out.println("Работа клиентского приложения завершена");
            System.exit(0);
        }
        if (command.equalsIgnoreCase("executeScript")){
            executeScript(arguments);
        }
        else {
            request = new Request(command, arguments);
        }
        sendAndReceive(request);
    }

    private void sendAndReceive(Request request) throws IOException, ClassNotFoundException {
        serverWriter.writeObject(request);
        System.out.println("запрос успешно отправлен");

        Response response = (Response) serverReader.readObject();
        System.out.println(response.getMessage());

        String collection = response.getCollection();
        if (!collection.isEmpty()){
            System.out.println(collection);
        }
    }

    private void executeScript(String path) throws ClassNotFoundException {
        if (path.isBlank()){
            System.out.println("Неверные аргументы команды");

        } else {
            try {
                Path pathToScript = Paths.get(path);

                PromptScan.setUserScanner(new Scanner(pathToScript));
                Scanner scriptScanner = PromptScan.getUserScanner();

                Path scriptFile = pathToScript.getFileName();

                if (!scriptScanner.hasNext()) throw new NoSuchElementException();

                scriptsNames.add(scriptFile);

                do {
                    var command = "";
                    var arguments = "";
                    String[] input = (scriptScanner.nextLine() + " ").trim().split(" ", 2);
                    if (input.length == 2){
                        arguments = input[1].trim();
                    }
                    command = input[0].trim();

                    while (scriptScanner.hasNextLine() && command.isEmpty()){
                        input = (scriptScanner.nextLine() + " ").trim().split(" ", 2);
                        if (input.length == 2){
                            arguments = input[1].trim();
                        }
                        command = input[0].trim();
                    }

                    if (command.equalsIgnoreCase("executeScript")) {

                        Path scriptNameFromArgument = Paths.get(arguments).getFileName();

                        if (scriptsNames.contains(scriptNameFromArgument)) {
                            throw new ScriptRecursionException("Один и тот же скрипт не может выполнятся рекурсивно");
                        }
                        executeScript(arguments);

                    }
                    else {
                        processUserPrompt(command, arguments);
                    }

                } while (scriptScanner.hasNextLine());

                scriptsNames.remove(scriptFile);
                PromptScan.setUserScanner(new Scanner(System.in));
                System.out.println("Скрипт " + scriptFile + " успешно выполнен");

            } catch (FileNotFoundException e){
                System.out.println("Файл " + path + " не найден");
            } catch (NoSuchElementException e){
                System.out.println("Файл " + path + " пуст");
            } catch (IllegalStateException e){
                System.out.println("Непредвиденная ошибка");
            } catch (SecurityException e){
                System.out.println("Недостаточно прав для чтения файла " + path);
            } catch (ScriptRecursionException e) {
                System.out.println(e.getMessage());
            } catch (IOException e) {
                System.out.println("Ошибка ввода/вывода");
            } catch (InvalidPathException e){
                System.out.println("Проверьте путь к файлу. В нём не должно быть лишних символов");
            }
        }

    }
}
