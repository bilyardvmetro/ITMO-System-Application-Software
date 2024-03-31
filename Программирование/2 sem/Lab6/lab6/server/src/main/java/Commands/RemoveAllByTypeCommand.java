package Commands;

import CollectionObject.VehicleModel;
import Modules.CommandHandler;
import Modules.ConsoleApp;
import Network.Response;

public class RemoveAllByTypeCommand implements Command{
    private CommandHandler commandHandler;

    public RemoveAllByTypeCommand(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
        ConsoleApp.commandList.put("removeAllByType", this);
    }

    @Override
    public Response execute(String arguments, VehicleModel objectArg) {
        return commandHandler.removeAllByType(arguments, objectArg);
    }
}
