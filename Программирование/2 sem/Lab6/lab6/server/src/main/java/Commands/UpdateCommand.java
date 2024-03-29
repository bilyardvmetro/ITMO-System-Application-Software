package Commands;

import CollectionObject.Vehicle;
import Modules.CommandHandler;
import Modules.ConsoleApp;
import Network.Response;

public class UpdateCommand implements Command{
    private CommandHandler commandHandler;

    public UpdateCommand(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
        ConsoleApp.commandList.put("update", this);
    }

    @Override
    public Response execute(String arguments, Vehicle objectArg) {
        return commandHandler.update(arguments, objectArg);
    }
}
