package Commands;

import CollectionObject.Vehicle;
import Modules.CommandHandler;
import Modules.ConsoleApp;
import Network.Response;

public class AddCommand implements Command {
    private CommandHandler commandHandler;

    public AddCommand(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
        ConsoleApp.commandList.put("add", this);
    }

    @Override
    public Response execute(String arguments, Vehicle objectArg) {
        return commandHandler.add(arguments, objectArg);
    }
}
