package Modules;

import CollectionObject.VehicleModel;
import Commands.*;
import Network.Request;
import Network.Response;


import java.io.*;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class Server {
    private InetSocketAddress address;
    private Selector selector;
    private ConsoleApp consoleApp;
    private Response response;
    private Request request;

    public Server(InetSocketAddress address) {
        this.address = address;
        consoleApp = createConsoleApp();
    }

    public void run(String[] args){

        try {
            var pathToCollection = args[0]; //"collection.csv"
            CSVProvider csvProvider = new CSVProvider(Path.of(pathToCollection));
            csvProvider.load();
            System.out.println("Коллекция загружена");

            selector = Selector.open();
            ServerSocketChannel serverChannel = ServerSocketChannel.open();
            serverChannel.bind(address);
            serverChannel.configureBlocking(false);
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);

            while (true) {
                selector.select();
                Iterator<SelectionKey> keys = selector.selectedKeys().iterator();

                while (keys.hasNext()){
                    SelectionKey key = keys.next();

                    try {
                        if (key.isValid()){
                            if (key.isAcceptable()){
                                ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
                                SocketChannel clientChannel = serverSocketChannel.accept();
                                System.out.println("Установлено соединение с клиентом " + clientChannel.socket().toString());
                                clientChannel.configureBlocking(false);
                                clientChannel.register(selector,SelectionKey.OP_READ);
                            }

                            if (key.isReadable()){
                                SocketChannel clientChannel = (SocketChannel) key.channel();
                                clientChannel.configureBlocking(false);

                                ByteBuffer clientData = ByteBuffer.allocate(2048);

                                System.out.println("\n" + clientChannel.read(clientData) + " байт пришло от клиента");
                                try(ObjectInputStream clientDataIn = new ObjectInputStream(new ByteArrayInputStream(clientData.array()))){
                                    request = (Request) clientDataIn.readObject();
                                };

                                var commandName = request.getCommandName();
                                var commandStrArg = request.getCommandStrArg();
                                var commandObjArg = (VehicleModel) request.getCommandObjArg();

                                if (ConsoleApp.commandList.containsKey(commandName)) {
                                    response = ConsoleApp.commandList.get(commandName).execute(commandStrArg, commandObjArg);
                                    CommandHandler.addCommand(commandName);
                                } else {
                                    response = new Response("Команда не найдена. Используйте help для справки", "");
                                }

                                clientChannel.register(selector, SelectionKey.OP_WRITE);
                            }

                            if (key.isWritable()){
                                SocketChannel clientChannel = (SocketChannel) key.channel();
                                clientChannel.configureBlocking(false);

                                try(ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                                    ObjectOutputStream clientDataOut = new ObjectOutputStream(bytes)){
                                    clientDataOut.writeObject(response);

                                    ByteBuffer clientData = ByteBuffer.wrap(bytes.toByteArray());
                                    ByteBuffer dataLength = ByteBuffer.allocate(32).putInt(clientData.limit());
                                    dataLength.flip();

                                    clientChannel.write(dataLength); // пишем длину ответа клиенту
                                    clientChannel.write(clientData); // шлём клиенту ответ
                                    clientData.clear();
                                }

                                clientChannel.register(selector, SelectionKey.OP_READ);
                            }
                        }
                    } catch (SocketException e){
                        System.out.println("Клиент " + key.channel().toString() + " отключился");
                        CommandHandler.save();
                        key.cancel();
                    }
                    keys.remove();
                }
            }
        } catch (ArrayIndexOutOfBoundsException ignored){
        } catch (NoSuchElementException e) {
            System.out.println("Остановка сервера через консоль");
            CommandHandler.save();
//            System.out.println(e.getMessage() + "\n" + Arrays.toString(e.getStackTrace())); // убрать
            System.exit(1);
        } catch (IOException e) {
            System.out.println("Ошибка ввода/вывода");
        } catch (ClassNotFoundException e) {
            System.out.println("Несоответствующие классы");
        }
    }

    private ConsoleApp createConsoleApp() {
        CommandHandler commandHandler = new CommandHandler();
        return new ConsoleApp(
                new HelpCommand(commandHandler),
                new InfoCommand(commandHandler),
                new ShowCommand(commandHandler),
                new AddCommand(commandHandler),
                new UpdateCommand(commandHandler),
                new RemoveByIdCommand(commandHandler),
                new ClearCommand(commandHandler),
                new RemoveGreaterCommand(commandHandler),
                new ReorderCommand(commandHandler),
                new HistoryCommand(commandHandler),
                new RemoveAllByTypeCommand(commandHandler),
                new CountGreaterThanEnginePowerCommand(commandHandler),
                new FilterStartsWithNameCommand(commandHandler)
        );
    }

}