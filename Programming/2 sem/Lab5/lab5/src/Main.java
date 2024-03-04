import Commands.*;

import Modules.CSVProvider;
import Modules.ConsoleApp;
import Modules.CommandHandler;

import java.nio.file.Path;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        ConsoleApp consoleApp = createConsoleApp();
        CSVProvider csvProvider = new CSVProvider(Path.of(args[0]));

        Scanner scanner = new Scanner(System.in);
        System.out.println("Это крутое консольное приложение запущенно специально для пацанов");
        consoleApp.help("");
        csvProvider.load();
//        csvProvider.load("collection.csv"); // для проверки из консоли
        System.out.print("> ");

        while (true){
            do {
                var command = "";
                var arguments = "";
                String[] input = (scanner.nextLine() + " ").trim().split(" ", 2);
                if (input.length == 2){
                    arguments = input[1].trim();
                }
                command = input[0].trim();

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
