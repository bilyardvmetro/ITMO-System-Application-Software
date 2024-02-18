package Modules;


import CollectionObject.VehicleType;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.Scanner;

public class CommandHandler {
    private CollectionService collectionService;
    private CSVProvider csvProvider;

    private static LinkedList<String> commandList = new LinkedList<>();

    public CommandHandler() {
        this.collectionService = new CollectionService();
        this.csvProvider = new CSVProvider();
    }

    public void help(String arguments){
        if (!arguments.isBlank()){
            System.out.println("гавно дибил");// illegal args exception
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

    // TODO: 19.02.2024 добавить исключения на аргументы
    public void info(String arguments){
        if (!arguments.isBlank()){
            System.out.println("гавно дибил"); // illegal args exception
        } else {
            collectionService.info();
        }
    }

    public void show(String arguments){
        if (!arguments.isBlank()){
            System.out.println("гавно дибил"); // illegal args exception
        } else {
            collectionService.show();
        }
    }

    public void add(String arguments){
        if (!arguments.isBlank()){
            System.out.println("гавно дибил"); // illegal args exception
        } else {
            collectionService.addElement();
        }
    }

    public void update(String arguments){  //args required
        if (arguments.isBlank()){
            System.out.println("гавно дибил"); // illegal args exception
        } else {
            String [] parsedArgs = arguments.split(" ");
            long current_id = Long.parseLong(parsedArgs[0]);
            String new_name = parsedArgs[1];

            collectionService.update(new_name, current_id);
        }
    }

    public void removeById(String arguments){ //args required
        if (arguments.isBlank()){
            System.out.println("гавно дибил"); // illegal args exception
        } else {
            long id = Long.parseLong(arguments);
            collectionService.removeById(id);
        }
    }

    public void clear(String arguments){
        if (!arguments.isBlank()){
            System.out.println("гавно дибил"); // illegal args exception
        } else {
            collectionService.clear();
        }
    }

    public void save(String arguments){
        if (!arguments.isBlank()){
            System.out.println("гавно дибил"); // illegal args exception
        } else {
            Scanner scanner = new Scanner(System.in);
            System.out.println(
                    "Введите имя файла в который хотите сохранить коллекцию"+ "\n" +
                    "(по умолчанию файлу будет дано имя collection.csv)"
            );
            String filename = scanner.nextLine();
            filename = filename.replaceAll(" ", "").replaceAll("\\.", "");
            if (filename.isBlank()){
                filename = "collection.csv";
            } else {
                filename += ".csv";
            }
            Path newFilePath = Paths.get(filename);
            csvProvider.save(newFilePath, collectionService.collection);
        }
    }

    public void executeScript(String path){  //args required
        if (path.isBlank()){
            System.out.println("гавно дибил"); // illegal args exception
        } else {

        }
        // TODO: 19.02.2024 реализовать executeScript
    }

    public void exit(String arguments){
        if (!arguments.isBlank()){
            System.out.println("гавно дибил"); // illegal args exception
        } else {
            System.exit(0);
        }
    }

    public void removeGreater(String arguments){ //args required
        if (arguments.isBlank()){
            System.out.println("гавно дибил"); // illegal args exception
        } else {
            long startId = Long.parseLong(arguments) + 1;
            collectionService.removeGreater(startId);
        }
    }

    public void reorder(String arguments){
        if (!arguments.isBlank()){
            System.out.println("гавно дибил"); // illegal args exception
        } else {
            collectionService.reorder();
        }
    }

    public void history(String arguments){
        if (!arguments.isBlank()){
            System.out.println("гавно дибил"); // illegal args exception
        } else {
            System.out.println("Последние 7 команд, введённые пользователем: ");
            for (String command : commandList) {
                System.out.println(command);
            }
        }
    }

    public void removeAllByType(String arguments){ //args required
        if (arguments.isBlank()){
            System.out.println("гавно дибил"); // illegal args exception
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
            System.out.println("гавно дибил"); // illegal args exception
        } else {
            double enginePower = Double.parseDouble(arguments);
            collectionService.countGreaterThanEnginePower(enginePower);
        }
    }

    public void filterStartsWithName(String arguments){ //args required
        if (arguments.isBlank()){
            System.out.println("гавно дибил"); // illegal args exception
        } else {
            collectionService.filterStartsWithName(arguments);
        }

    }


    public static void addCommand(String command){
        if (commandList.size() == 7){
            commandList.removeFirst();
        }
        commandList.addLast(command);
    }
}
