package Commands;

import Modules.CommandHandler;
import Modules.ConsoleApp;

public class FilterStartsWithNameCommand implements Command{
    private CommandHandler commandHandler;

    public FilterStartsWithNameCommand(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
        ConsoleApp.commandList.put("filterStartsWithName", this);
    }

    @Override
    public void execute(String arguments) {
        commandHandler.filterStartsWithName(arguments);
    }
}
