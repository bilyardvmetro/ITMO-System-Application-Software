package Commands;

import CollectionObject.VehicleModel;
import Modules.CommandHandler;
import Modules.ConsoleApp;
import Network.Response;
import Network.User;

public class RemoveAllByTypeCommand implements Command{
    private CommandHandler commandHandler;

    public RemoveAllByTypeCommand(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
        ConsoleApp.commandList.put("removeAllByType", this);
    }

    @Override
    public Response execute(User user, String arguments, VehicleModel objectArg) {
        return commandHandler.removeAllByType(user,arguments, objectArg);
    }
}
