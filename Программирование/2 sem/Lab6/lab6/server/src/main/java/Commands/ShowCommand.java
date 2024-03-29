package Commands;

import CollectionObject.Vehicle;
import Modules.CommandHandler;
import Modules.ConsoleApp;
import Network.Response;

public class ShowCommand implements Command{
    private CommandHandler commandHandler;

    public ShowCommand(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
        ConsoleApp.commandList.put("show", this);
    }

    @Override
    public Response execute(String arguments, Vehicle objectArg) {
        return commandHandler.show(arguments, objectArg);
    }
}
