package Commands;

import CollectionObject.VehicleModel;
import Modules.CommandHandler;
import Modules.ConsoleApp;
import Network.Response;

public class RemoveByIdCommand implements Command{
    private CommandHandler commandHandler;

    public RemoveByIdCommand(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
        ConsoleApp.commandList.put("removeById", this);
    }

    @Override
    public Response execute(String arguments, VehicleModel objectArg) {
        return commandHandler.removeById(arguments, objectArg);
    }
}
