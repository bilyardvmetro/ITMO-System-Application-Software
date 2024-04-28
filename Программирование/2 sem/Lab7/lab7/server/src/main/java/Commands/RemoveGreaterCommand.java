package Commands;

import CollectionObject.VehicleModel;
import Modules.CommandHandler;
import Modules.ConsoleApp;
import Network.Response;

public class RemoveGreaterCommand implements Command{
    private CommandHandler commandHandler;

    public RemoveGreaterCommand(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
        ConsoleApp.commandList.put("removeGreater", this);
    }

    @Override
    public Response execute(String arguments, VehicleModel objectArg) {
        return commandHandler.removeGreater(arguments, objectArg);
    }
}
