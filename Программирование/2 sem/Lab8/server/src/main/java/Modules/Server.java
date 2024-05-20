package Modules;

import CollectionObject.VehicleModel;
import Commands.*;
import Network.Request;
import Network.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Stack;
import java.util.concurrent.ForkJoinPool;

public class Server {
    private InetSocketAddress address;
    private Selector selector;
    private ConsoleApp consoleApp;
    private ForkJoinPool forkJoinPool;
    private volatile Response response;
    private Request request;
    protected static Logger logger;

    public Server(InetSocketAddress address) {
        this.address = address;
        this.consoleApp = createConsoleApp();
        logger = LogManager.getLogger(Server.class);
        this.forkJoinPool = ForkJoinPool.commonPool();
    }

    public void run() {

        try {
            DBProvider.load();
            logger.info("Коллекция загружена успешно");

            selector = Selector.open();

            ServerSocketChannel serverChannel = ServerSocketChannel.open();
            serverChannel.bind(address);
            serverChannel.configureBlocking(false);

            logger.info("Канал сервера готов к работе");
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);

            while (true) {
                selector.select();
                Iterator<SelectionKey> keys = selector.selectedKeys().iterator();

                while (keys.hasNext()) {
                    SelectionKey key = keys.next();
                    logger.info("Обработка ключа началась");

                    try {
                        if (key.isValid()) {

                            if (key.isAcceptable()) {
                                ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
                                SocketChannel clientChannel = serverSocketChannel.accept();

                                logger.info("Установлено соединение с клиентом " + clientChannel.socket().toString());

                                clientChannel.configureBlocking(false);
                                clientChannel.register(selector, SelectionKey.OP_READ);
                            }

                            if (key.isReadable()) {
                                SocketChannel clientChannel = (SocketChannel) key.channel();
                                clientChannel.configureBlocking(false);

                                if (clientChannel.isOpen()) {

                                    forkJoinPool.submit(() -> {
                                    try {
                                        readRequest(clientChannel, key);

                                    } catch (SocketException e) {
                                        key.cancel();

                                    } catch (IOException e) {
                                        logger.error("Ошибка ввода вывода");
                                        e.printStackTrace();

                                    } catch (ClassNotFoundException e) {
                                        e.printStackTrace();
                                        logger.error("Class cast ошибка");
                                    }
                                }).join();
                                    if (key.isValid()){
                                        Thread processRequestThread = new Thread(this::processRequest);
                                        processRequestThread.start();
                                        processRequestThread.join();
                                    }
                                }

                                clientChannel.register(selector, SelectionKey.OP_WRITE);
                            }

                            if (key.isWritable()) {
                                SocketChannel clientChannel = (SocketChannel) key.channel();
                                clientChannel.configureBlocking(false);

                                Thread sendThread = new Thread(() -> {
                                    try {
                                        sendResponse(clientChannel);

                                    } catch (SocketException e) {
                                        logger.info("Клиент " + key.channel().toString() + " отключился");
                                        key.cancel();

                                    } catch (IOException e) {
                                        logger.error("Ошибка ввода вывода");
                                        e.printStackTrace();
                                    }
                                });
                                sendThread.start();
                                sendThread.join();

                                clientChannel.register(selector, SelectionKey.OP_READ);
                            }
                        }
                    } catch (CancelledKeyException e) {
                        logger.info("Клиент " + key.channel().toString() + " отключился");

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        logger.error("Поток обработки запроса или отправки запроса был прерван");
                    }
                    keys.remove();
                }
            }
        } catch (ArrayIndexOutOfBoundsException ignored) {
        } catch (NoSuchElementException e) {
            logger.error("Остановка сервера через консоль");
            System.exit(1);
        } catch (IOException e) {
            logger.error("Ошибка ввода/вывода");
        }
    }

    private synchronized void readRequest(SocketChannel clientChannel, SelectionKey key) throws IOException, ClassNotFoundException {
        ByteBuffer clientData = ByteBuffer.allocate(2048);

        logger.info(clientChannel.read(clientData) + " байт пришло от клиента");
        try (ObjectInputStream clientDataIn = new ObjectInputStream(new ByteArrayInputStream(clientData.array()))) {
            request = (Request) clientDataIn.readObject();
        } catch (StreamCorruptedException e) {
            key.cancel();
        }
    }

    private synchronized void processRequest() {
        if (request.getCommandName() == null) {
            var user = request.getUser();

            if (!request.userRegisterRequired()) {
                if (DBProvider.checkUserExistence(user.getUsername())) {
                    if (DBProvider.checkUserPassword(user)) {
                        response = new Response("Дарова, " + user.getUsername() + "\n", true);
                        logger.info("Пользователь " + user.getUsername() + " успешно аутентифицирован");

                    } else {
                        response = new Response("Пароль введён неверно", false);
                        logger.info("Пользователь " + user.getUsername() + " неверно ввёл пароль");
                    }

                } else {
                    response = new Response("Пользователя " + user.getUsername() + " не существует", false);
                    logger.info("Пользователя " + user.getUsername() + " не существует");
                }

            } else {
                if (DBProvider.checkUserExistence(user.getUsername())){
                    response = new Response("Такой логин уже занят", false);
                } else {
                    DBProvider.addUser(user);
                    response = new Response("Добро пожаловать в клуб, " + user.getUsername() + "\n", true);
                    logger.info("Пользователь " + user.getUsername() + " успешно зарегистрирован");
                }
            }

        } else {
            var commandName = request.getCommandName();
            var commandStrArg = request.getCommandStrArg();
            var commandObjArg = (VehicleModel) request.getCommandObjArg();
            var user = request.getUser();

            if (ConsoleApp.commandList.containsKey(commandName)) {
                response = ConsoleApp.commandList.get(commandName).execute(user, commandStrArg, commandObjArg);
                CommandHandler.addCommand(commandName);
            } else {
                response = new Response("Команда не найдена. Используйте help для справки", new Stack<>());
            }

            logger.info("Запрос:\n" + commandName + "\n" + commandStrArg + "\n" + commandObjArg + "\nУспешно обработан");
        }
    }

    private synchronized void sendResponse(SocketChannel clientChannel) throws IOException {
        try (ByteArrayOutputStream bytes = new ByteArrayOutputStream();
             ObjectOutputStream clientDataOut = new ObjectOutputStream(bytes)) {
            clientDataOut.writeObject(response);

            var byteResponse = bytes.toByteArray();

            ByteBuffer dataLength = ByteBuffer.allocate(8).putInt(byteResponse.length);
            dataLength.flip();
            clientChannel.write(dataLength);
            logger.info("Отправлен пакет с длинной сообщения");

            while(byteResponse.length > 256){
                ByteBuffer packet = ByteBuffer.wrap(Arrays.copyOfRange(byteResponse, 0, 256));
                clientChannel.write(packet);
                byteResponse = Arrays.copyOfRange(byteResponse, 256, byteResponse.length);
                logger.info("Отправлен пакет байтов длины: " + packet.position());
            }
            ByteBuffer packet = ByteBuffer.wrap(byteResponse);
            clientChannel.write(packet);
            Thread.sleep(300);
            logger.info("Отправлен последний пакет байтов длины: " + packet.position());
            ByteBuffer stopPacket = ByteBuffer.wrap(new byte[]{28, 28});
            clientChannel.write(stopPacket);
            logger.info("Отправлен стоп пакет\n");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
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