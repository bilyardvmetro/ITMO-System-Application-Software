package Commands;

import CollectionObject.Vehicle;
import Modules.CommandHandler;
import Modules.ConsoleApp;
import Network.Response;

public class ExecuteScriptCommand implements Command{
    private CommandHandler commandHandler;

    public ExecuteScriptCommand(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
        ConsoleApp.commandList.put("executeScript", this);
    }

    @Override
    public Response execute(String arguments, Vehicle objectArg) {
        return commandHandler.executeScript(arguments, objectArg);
    }
}
