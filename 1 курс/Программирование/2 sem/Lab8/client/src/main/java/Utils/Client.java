package Utils;

import CollectionObject.VehicleModel;
import Exceptions.ScriptRecursionException;
import Network.Request;
import Network.Response;
import Network.User;

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
import java.security.NoSuchAlgorithmException;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

public class Client {
    private Set<Path> scriptsNames = new TreeSet<>();
    private InetAddress host;
    private int port;
    private SocketChannel channel;
    private User user;

    public Client(InetAddress host, int port) {
        this.host = host;
        this.port = port;
    }

    public void connect() throws ConnectException{
        try {
            SocketAddress address = new InetSocketAddress(host, port);
            channel = SocketChannel.open();
            channel.connect(address);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Response authenticateUser(String username, String password){
        try {
            user = new User(username, PasswordHasher.getHash(password));

            var userAuthenticationRequest = new Request(user, false);

            return sendAndReceive(userAuthenticationRequest);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Response registerUser(String username, String password) throws IOException, ClassNotFoundException, NoSuchAlgorithmException {

        user = new User(username, PasswordHasher.getHash(password));

        var userAuthenticationRequest = new Request(user, true);

        return sendAndReceive(userAuthenticationRequest);
    }

    private void processScriptCommand(String command, String arguments) throws IOException, ClassNotFoundException {
        Request request;
        Response response;
        if (command.equalsIgnoreCase("add") || command.equalsIgnoreCase("update")){
            VehicleModel objArgument = VehicleAsker.createElement(user);
            request = new Request(user, command, arguments, objArgument);
            response = sendAndReceive(request);
            System.out.println(response.getMessage());
        }
        else if (command.equalsIgnoreCase("exit")){
            System.exit(0);
        }
        else if (command.equalsIgnoreCase("executeScript")){
            executeScript(arguments);
        }
        else {
            request = new Request(user, command, arguments);
            response = sendAndReceive(request);
            System.out.println(response.getMessage());
        }
    }

    public synchronized Response sendAndReceive(Request request) throws IOException, ClassNotFoundException {

        try(ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bytes)) {

            out.writeObject(request);
            ByteBuffer dataToSend = ByteBuffer.wrap(bytes.toByteArray());
            channel.write(dataToSend); // отправляем серверу запрос
            out.flush();
        }

        ByteBuffer dataToReceiveLength = ByteBuffer.allocate(8);
        channel.read(dataToReceiveLength); // читаем длину ответа от сервера
        dataToReceiveLength.flip();
        int responseLength = dataToReceiveLength.getInt(); // достаём её из буфера

        ByteBuffer responseBytes = ByteBuffer.allocate(responseLength); // создаем буфер нужной нам длины
        ByteBuffer packetFromServer = ByteBuffer.allocate(256);

        while (true){
            channel.read(packetFromServer);
            if (packetFromServer.position() == 2 && packetFromServer.get(0) == 28 && packetFromServer.get(1) == 28) break;
            packetFromServer.flip();
            responseBytes.put(packetFromServer);
            packetFromServer.clear();
        }

        try(ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(responseBytes.array()))){
            return (Response) in.readObject();
        }
    }


    public void executeScript(String path) throws ClassNotFoundException, IOException, NoSuchElementException, InvalidPathException, SecurityException {
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

                } catch (ScriptRecursionException ignored){}
            }
            else {
                processScriptCommand(command, arguments);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        } while (scriptScanner.hasNextLine());

        scriptsNames.remove(scriptFile);
        PromptScan.setUserScanner(new Scanner(System.in));
    }

    public User getUser() {
        return user;
    }

}
