package Commands;

import CollectionObject.VehicleModel;
import Modules.CommandHandler;
import Modules.ConsoleApp;
import Network.Response;
import Network.User;

public class HistoryCommand implements Command{
    private CommandHandler commandHandler;

    public HistoryCommand(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
        ConsoleApp.commandList.put("history", this);
    }

    @Override
    public Response execute(User user, String arguments, VehicleModel objectArg) {
        return commandHandler.history(user,arguments, objectArg);
    }
}
