package Modules;


import CollectionObject.VehicleType;
import Exceptions.ScriptRecursionException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;


public class CommandHandler {
    private CollectionService collectionService;
    private static CSVProvider csvProvider;
    private static LinkedList<String> commandHistory = new LinkedList<>();
    private static Set<Path> scriptsNames = new TreeSet<>();

    public CommandHandler() {
        this.collectionService = new CollectionService();
        csvProvider = new CSVProvider();
    }

    public void help(String arguments){
        if (!arguments.isBlank()){
            System.out.println("Неверные аргументы команды");// illegal args exception
        } else {
            System.out.println(
                """
                Список доступных команд:
                ================================================================================================
                help - справка по командам
                info - вывод данных о коллекции (тип, дата инициализации, количество элементов)
                show - вывести все элементы коллекции
                add <el> - добавить элемент в коллекцию
                update <id> <el> - обновить id элемента на заданный
                removeById <id> - удалить элемент по id
                clear - очистить всю коллекцию
                save - сохранить коллекцию
                executeScript <path> - исполнить скрипт
                exit - закрыть приложение без сохранения данных
                removeGreater <el> - удалить из коллекции все элементы, превышающие данный
                reorder - отсортировать коллекцию в порядке, обратном нынешнему
                history - вывести последние 7 команд
                removeAllByType <type> - удалить из коллекции все элементы, значение поля которых равно заданному
                countGreaterThanEnginePower <engine_power> - вывести количество элементов коллекции, мощность которых выше заданной
                filterStartsWithName <name> - вывести все элементы, значение поля name которых начинается с заданной подстроки
                ================================================================================================
                """
            );
        }
    }

    public void info(String arguments){
        if (!arguments.isBlank()){
            System.out.println("Неверные аргументы команды"); // illegal args exception
        } else {
            collectionService.info();
        }
    }

    public void show(String arguments){
        if (!arguments.isBlank()){
            System.out.println("Неверные аргументы команды"); // illegal args exception
        } else {
            collectionService.show();
        }
    }

    public void add(String arguments){
        if (!arguments.isBlank()){
            System.out.println("Неверные аргументы команды"); // illegal args exception
        } else {
            collectionService.addElement();
        }
    }

    public void update(String arguments){  //args required
        if (arguments.isBlank()){
            System.out.println("Неверные аргументы команды"); // illegal args exception
        } else {
            try {
                long current_id = Long.parseLong(arguments);
                if (current_id > 0){
                    collectionService.update(current_id);
                } else {
                    System.out.println("id не может быть отрицательным");
                }

            } catch (NumberFormatException e){
                System.out.println("Неверный формат аргументов");
            }
        }
    }

    public void removeById(String arguments){ //args required
        if (arguments.isBlank()){
            System.out.println("Неверные аргументы команды"); // illegal args exception
        } else {
            try {
                long id = Long.parseLong(arguments);
                if (id > 0){
                    collectionService.removeById(id);
                } else {
                    System.out.println("id не может быть отрицательным");
                }
            } catch (NumberFormatException e){
                System.out.println("Неверный формат аргументов");
            }
        }
    }

    public void clear(String arguments){
        if (!arguments.isBlank()){
            System.out.println("Неверные аргументы команды"); // illegal args exception
        } else {
            collectionService.clear();
        }
    }

    public void save(String arguments){
        if (!arguments.isBlank()){
            System.out.println("Неверные аргументы команды"); // illegal args exception
        } else {
            csvProvider.save(CollectionService.collection);
        }
    }

    public void executeScript(String path){  //args required
        if (path.isBlank()){
            System.out.println("Неверные аргументы команды"); // illegal args exception
        } else {
            try {
                Path pathToScript = Paths.get(path);
                Scanner scriptScanner = new Scanner(pathToScript);
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

                    if (ConsoleApp.commandList.containsKey(command)){

                        if (command.equalsIgnoreCase("executeScript")){

                            Path scriptNameFromArgument = Paths.get(arguments).getFileName();

                            if (scriptsNames.contains(scriptNameFromArgument)) {
                                throw new ScriptRecursionException("Один и тот же скрипт не может выполнятся рекурсивно");
                            }
                            ConsoleApp.commandList.get("executeScript").execute(arguments);

                        } else {
                            ConsoleApp.commandList.get(command).execute(arguments);
                            System.out.println("Команда " + command + " выполнена успешно");
                        }
                    } else {
                        System.out.println("Неизвестная команда. Ты по-моему перепутал...");
                    }

                } while (scriptScanner.hasNextLine());
                scriptsNames.remove(scriptFile);
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

    public void exit(String arguments){
        if (!arguments.isBlank()){
            System.out.println("Неверные аргументы команды"); // illegal args exception
        } else {
            System.out.println(
            """
            Если вы выйдете, изменения не сохранятся. Вы уверены, что хотите выйти?
            y = "Да"      любая клавиша = "Нет"
            """);
            Scanner scanner = new Scanner(System.in);
            var answer = scanner.nextLine();
            if (answer.equalsIgnoreCase("y")){
                System.exit(0);
            }
        }
    }

    public void removeGreater(String arguments){ //args required
        if (arguments.isBlank()){
            System.out.println("Неверные аргументы команды"); // illegal args exception
        } else {
            try {
                long startId = Long.parseLong(arguments) + 1;
                if (startId > 0) {
                    collectionService.removeGreater(startId);
                } else {
                    System.out.println("id не может быть отрицательным");
                }
            } catch (NumberFormatException e){
                System.out.println("Неверный формат аргументов");
            }
        }
    }

    public void reorder(String arguments){
        if (!arguments.isBlank()){
            System.out.println("Неверные аргументы команды"); // illegal args exception
        } else {
            collectionService.reorder();
        }
    }

    public void history(String arguments){
        if (!arguments.isBlank()){
            System.out.println("Неверные аргументы команды"); // illegal args exception
        } else {
            System.out.println("Последние 7 команд, введённые пользователем: ");
            for (String command : commandHistory) {
                System.out.println(command);
            }
        }
    }

    public void removeAllByType(String arguments){ //args required
        if (arguments.isBlank()){
            System.out.println("Неверные аргументы команды"); // illegal args exception
        } else {
            try {
                VehicleType type = VehicleType.valueOf(arguments.toUpperCase());
                collectionService.removeAllByType(type);
            } catch (IllegalArgumentException e){
                System.out.println("Такого типа транспортных средств не существует");
            }
        }
    }

    public void countGreaterThanEnginePower(String arguments){ //args required
        if (arguments.isBlank()){
            System.out.println("Неверные аргументы команды"); // illegal args exception
        } else {
            try {
                double enginePower = Double.parseDouble(arguments);
                if (enginePower > 0) {
                    collectionService.countGreaterThanEnginePower(enginePower);
                } else {
                    System.out.println("Мощность двигателя не может быть отрицательной");
                }
            } catch (NumberFormatException e){
                System.out.println("Неверный формат аргументов");
            }
        }
    }

    public void filterStartsWithName(String arguments){ //args required
        if (arguments.isBlank()){
            System.out.println("Неверные аргументы команды"); // illegal args exception
        } else {
            collectionService.filterStartsWithName(arguments);
        }

    }


    public static void addCommand(String command){
        if (commandHistory.size() == 7){
            commandHistory.removeFirst();
        }
        commandHistory.addLast(command);
    }
}
