package Commands;

import Modules.CommandHandler;
import Modules.ConsoleApp;

public class RemoveGreaterCommand implements Command{
    private CommandHandler commandHandler;

    public RemoveGreaterCommand(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
        ConsoleApp.commandList.put("removeGreater", this);
    }

    @Override
    public void execute(String arguments) {
        commandHandler.removeGreater(arguments);
    }
}
