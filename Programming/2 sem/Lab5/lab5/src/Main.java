import Commands.*;

import Modules.CSVProvider;
import Modules.ConsoleApp;
import Modules.CommandHandler;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        ConsoleApp consoleApp = createConsoleApp();
        CSVProvider csvProvider = new CSVProvider();

        Scanner scanner = new Scanner(System.in);
        System.out.println("Это крутое консольное приложение запущенно специально для пацанов");
        consoleApp.help("");
        csvProvider.load(args[0]);
//        csvProvider.load("collection.csv"); // для проверки из консоли
        System.out.print("> ");

        while (true){
            do {
                String commandLine = scanner.nextLine();
                String [] parsedCommand = commandLine.split(" ", 2);
                String command = "";
                String arguments = "";
                if (parsedCommand.length == 1){
                    command = parsedCommand[0];
                }
                else if (parsedCommand.length == 2) {
                    command = parsedCommand[0];
                    arguments = parsedCommand[1];
                }
                else {
                    System.out.println("Некорректный формат аргументов");
                }

                if (ConsoleApp.commandList.containsKey(command)){
                    ConsoleApp.commandList.get(command).execute(arguments);
                    CommandHandler.addCommand(command);
                } else {
                    System.out.println("Неизвестная команда. Ты по-моему перепутал...");
                }
                System.out.print("> ");
            } while (scanner.hasNextLine());
        }
    }

    private static ConsoleApp createConsoleApp() {
        CommandHandler commandHandler = new CommandHandler();
        return new ConsoleApp(
                new HelpCommand(commandHandler),
                new InfoCommand(commandHandler),
                new ShowCommand(commandHandler),
                new AddCommand(commandHandler),
                new UpdateCommand(commandHandler),
                new RemoveByIdCommand(commandHandler),
                new ClearCommand(commandHandler),
                new SaveCommand(commandHandler),
                new ExecuteScriptCommand(commandHandler),
                new ExitCommand(commandHandler),
                new RemoveGreaterCommand(commandHandler),
                new ReorderCommand(commandHandler),
                new HistoryCommand(commandHandler),
                new RemoveAllByTypeCommand(commandHandler),
                new CountGreaterThanEnginePowerCommand(commandHandler),
                new FilterStartsWithNameCommand(commandHandler)
        );
    }

}
