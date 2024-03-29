package Commands;

import CollectionObject.Vehicle;
import Modules.CommandHandler;
import Modules.ConsoleApp;
import Network.Response;

public class CountGreaterThanEnginePowerCommand implements Command {
    private CommandHandler commandHandler;

    public CountGreaterThanEnginePowerCommand(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
        ConsoleApp.commandList.put("countGreaterThanEnginePower", this);
    }

    @Override
    public Response execute(String arguments, Vehicle objectArg) {
        return commandHandler.countGreaterThanEnginePower(arguments, objectArg);
    }
}
