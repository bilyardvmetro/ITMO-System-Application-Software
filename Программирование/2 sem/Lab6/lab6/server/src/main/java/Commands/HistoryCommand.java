package Commands;

import CollectionObject.Vehicle;
import Modules.CommandHandler;
import Modules.ConsoleApp;
import Network.Response;

public class HistoryCommand implements Command{
    private CommandHandler commandHandler;

    public HistoryCommand(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
        ConsoleApp.commandList.put("history", this);
    }

    @Override
    public Response execute(String arguments, Vehicle objectArg) {
        return commandHandler.history(arguments, objectArg);
    }
}
