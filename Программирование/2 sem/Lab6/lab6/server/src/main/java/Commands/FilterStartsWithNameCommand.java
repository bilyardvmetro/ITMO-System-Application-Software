package Commands;

import CollectionObject.Vehicle;
import Modules.CommandHandler;
import Modules.ConsoleApp;
import Network.Response;

public class FilterStartsWithNameCommand implements Command{
    private CommandHandler commandHandler;

    public FilterStartsWithNameCommand(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
        ConsoleApp.commandList.put("filterStartsWithName", this);
    }

    @Override
    public Response execute(String arguments, Vehicle objectArg) {
        return commandHandler.filterStartsWithName(arguments, objectArg);
    }
}
