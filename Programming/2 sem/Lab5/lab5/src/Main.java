import Commands.*;

import Modules.ConsoleApp;
import Modules.CommandHandler;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        ConsoleApp consoleApp = createConsoleApp();

        Scanner scanner = new Scanner(System.in);
        System.out.println("Это крутое консольное приложение запущенно специально для пацанов");
        consoleApp.help("");

        while (true){
            System.out.print("> ");
            String commandLine = scanner.nextLine();
            String [] parsedCommand = commandLine.split(" ");
            String command = "";
            String arguments = "";
            if (parsedCommand.length == 1){
                command = parsedCommand[0];
            }
            if (parsedCommand.length == 2) {
                command = parsedCommand[0];
                arguments = parsedCommand[1];
            }
            if (parsedCommand.length == 3) {
                command = parsedCommand[0];
                arguments = parsedCommand[1] + " " + parsedCommand[2];
            }

            if (command.equalsIgnoreCase("help")){
                consoleApp.help(arguments);
                CommandHandler.addCommand(command);
            }
            if (command.equalsIgnoreCase("info")){
                consoleApp.info(arguments);
                CommandHandler.addCommand(command);
            }
            if (command.equalsIgnoreCase("show")){
                consoleApp.show(arguments);
                CommandHandler.addCommand(command);
            }
            if (command.equalsIgnoreCase("add")){
                consoleApp.add(arguments);
                CommandHandler.addCommand(command);
            }
            if (command.equalsIgnoreCase("update")){ // меняет имя объекта
                consoleApp.update(arguments);
                CommandHandler.addCommand(command);
            }
            if (command.equalsIgnoreCase("removeById")){
                consoleApp.removeById(arguments);
                CommandHandler.addCommand(command);
            }
            if (command.equalsIgnoreCase("clear")){
                consoleApp.clear(arguments);
                CommandHandler.addCommand(command);
            }
            if (command.equalsIgnoreCase("save")){
                consoleApp.save(arguments);
                CommandHandler.addCommand(command);
            }
            if (command.equalsIgnoreCase("executeScript")){
                consoleApp.executeScript(arguments);
                CommandHandler.addCommand(command);
            }
            if (command.equalsIgnoreCase("exit")){
                consoleApp.exit(arguments);
                CommandHandler.addCommand(command);
            }
            if (command.equalsIgnoreCase("removeGreater")){
                consoleApp.removeGreater(arguments);
                CommandHandler.addCommand(command);
            }
            if (command.equalsIgnoreCase("reorder")){
                consoleApp.reorder(arguments);
                CommandHandler.addCommand(command);
            }
            if (command.equalsIgnoreCase("history")){
                consoleApp.history(arguments);
                CommandHandler.addCommand(command); // должна ли учитываться сама команда history???
            }
            if (command.equalsIgnoreCase("removeAllByType")){
                consoleApp.removeAllByType(arguments);
                CommandHandler.addCommand(command); // должна ли учитываться сама команда history???
            }
            if (command.equalsIgnoreCase("filterStartsWithName")){
                consoleApp.filterStartsWithName(arguments);
                CommandHandler.addCommand(command);
            }
            if (command.equalsIgnoreCase("countGreaterThanEnginePower")){
                consoleApp.countGreaterThanEnginePower(arguments);
                CommandHandler.addCommand(command);
            }

//            else {
//                System.out.println("Неизвестная команда. Ты по-моему перепутал...");
//            } todo: починить обработку неизвестной команды
        }

    }

    private static ConsoleApp createConsoleApp() {
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
        return consoleApp;
    }
}
