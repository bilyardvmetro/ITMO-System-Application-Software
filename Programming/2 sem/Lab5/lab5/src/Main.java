import Commands.*;

import Modules.ConsoleApp;
import Modules.CommandHandler;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        CommandHandler commandHandler = new CommandHandler();
        ConsoleApp consoleApp = new ConsoleApp(
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

        Scanner scanner = new Scanner(System.in);
        System.out.println("Это крутое консольное приложение запущенно специально для пацанов");
        consoleApp.help();

        while (true){
            System.out.print("> ");
            String command = scanner.nextLine();
            if (command.toLowerCase().startsWith("help")){
                consoleApp.help();
            }
            if (command.toLowerCase().startsWith("add")){
                consoleApp.add();
            }
            if (command.toLowerCase().startsWith("exit")){
                consoleApp.exit();
            }
        }

    }
}
