package Commands;

import CollectionObject.Vehicle;
import Modules.CommandHandler;
import Modules.ConsoleApp;
import Network.Response;

public class ReorderCommand implements Command{
    private CommandHandler commandHandler;

    public ReorderCommand(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
        ConsoleApp.commandList.put("reorder", this);
    }

    @Override
    public Response execute(String arguments, Vehicle objectArg) {
        return commandHandler.reorder(arguments, objectArg);
    }
}
