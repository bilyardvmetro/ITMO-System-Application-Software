package Commands;

import CollectionObject.VehicleModel;
import Modules.CommandHandler;
import Modules.ConsoleApp;
import Network.Response;
import Network.User;

public class FilterStartsWithNameCommand implements Command{
    private CommandHandler commandHandler;

    public FilterStartsWithNameCommand(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
        ConsoleApp.commandList.put("filterStartsWithName", this);
    }

    @Override
    public Response execute(User user, String arguments, VehicleModel objectArg) {
        return commandHandler.filterStartsWithName(user,arguments, objectArg);
    }
}
