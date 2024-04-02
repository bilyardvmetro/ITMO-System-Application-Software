package util;

import CollectionObject.VehicleModel;
import Exceptions.ScriptRecursionException;
import Network.Request;
import Network.Response;

import java.io.*;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Client {
    private Set<Path> scriptsNames = new TreeSet<>();
    private InetAddress host;
    private int port;
    private SocketChannel channel;

    public Client(InetAddress host, int port) {
        this.host = host;
        this.port = port;
    }

    public void run(){
        try {
            SocketAddress address = new InetSocketAddress(host, port);
            channel = SocketChannel.open();
            channel.connect(address);

            PromptScan.setUserScanner(new Scanner(System.in));
            var scanner = PromptScan.getUserScanner();

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
                        System.out.print("> ");
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
        Request request;
        if (command.equalsIgnoreCase("add") || command.equalsIgnoreCase("update")){
            VehicleModel objArgument = VehicleAsker.createElement();
            request = new Request(command, arguments, objArgument);
            sendAndReceive(request);
        }
        else if (command.equalsIgnoreCase("exit")){
            System.out.println("Работа клиентского приложения завершена");
            System.exit(0);
        }
        else if (command.equalsIgnoreCase("executeScript")){
            executeScript(arguments);
        }
        else {
            request = new Request(command, arguments);
            sendAndReceive(request);
        }
    }

    private void sendAndReceive(Request request) throws IOException, ClassNotFoundException {
        try(ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bytes)) {

            out.writeObject(request);
            ByteBuffer dataToSend = ByteBuffer.wrap(bytes.toByteArray());
            channel.write(dataToSend);
//            System.out.println("\n" + channel.write(dataToSend) + " отправлено серверу");
            out.flush();
        }
        System.out.println("запрос успешно отправлен");

        ByteBuffer dataToReceive = ByteBuffer.allocate(4096);
        channel.read(dataToReceive);
//        System.out.println("\n" + channel.read(dataToReceive) + " байт пришло от сервера");

        try(ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(dataToReceive.array()))){
            Response response = (Response) in.readObject();

            System.out.println(response.getMessage());
            String collection = response.getCollection();
            if (!collection.isEmpty()){
                System.out.println(collection);
            }

        } catch (EOFException e){
//            e.printStackTrace();
            System.out.println("Ответ от сервера превысил величину буфера");
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
                        try {
                            Path scriptNameFromArgument = Paths.get(arguments).getFileName();

                            if (scriptsNames.contains(scriptNameFromArgument)) {
                                throw new ScriptRecursionException("Один и тот же скрипт не может выполнятся рекурсивно");
                            }
                            executeScript(arguments);

                        } catch (ScriptRecursionException e) {
                            System.out.println(e.getMessage());
                        }

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
            } catch (IOException e) {
                System.out.println("Ошибка ввода/вывода");
                e.printStackTrace();
            } catch (InvalidPathException e){
                System.out.println("Проверьте путь к файлу. В нём не должно быть лишних символов");
            }
        }

    }
}