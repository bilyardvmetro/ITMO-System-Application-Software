package Commands;

import CollectionObject.Vehicle;
import Modules.CommandHandler;
import Modules.ConsoleApp;
import Network.Response;

public class ExitCommand implements Command{
    private CommandHandler commandHandler;

    public ExitCommand(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
        ConsoleApp.commandList.put("exit", this);
    }

    @Override
    public Response execute(String arguments, Vehicle objectArg) {
        return commandHandler.exit(arguments, objectArg);
    }
}
