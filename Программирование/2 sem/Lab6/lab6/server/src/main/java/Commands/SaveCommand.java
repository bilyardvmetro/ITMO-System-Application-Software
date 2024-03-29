package Commands;

import CollectionObject.Vehicle;
import Modules.CommandHandler;
import Modules.ConsoleApp;
import Network.Response;

public class SaveCommand implements Command{
    private CommandHandler commandHandler;

    public SaveCommand(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
        ConsoleApp.commandList.put("save", this);
    }

    @Override
    public Response execute(String arguments, Vehicle objectArg) {
        return commandHandler.save(arguments, objectArg);
    }
}
