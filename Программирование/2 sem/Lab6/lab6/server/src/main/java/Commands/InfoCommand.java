package Commands;

import CollectionObject.Vehicle;
import Modules.CommandHandler;
import Modules.ConsoleApp;
import Network.Response;

public class InfoCommand implements Command{
    private CommandHandler commandHandler;

    public InfoCommand(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
        ConsoleApp.commandList.put("info", this);
    }

    @Override
    public Response execute(String arguments, Vehicle objectArg) {
        return commandHandler.info(arguments, objectArg);
    }
}
