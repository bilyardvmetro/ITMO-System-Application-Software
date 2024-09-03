package Commands;

import Modules.CommandHandler;
import Modules.ConsoleApp;

public class ReorderCommand implements Command{
    private CommandHandler commandHandler;

    public ReorderCommand(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
        ConsoleApp.commandList.put("reorder", this);
    }

    @Override
    public void execute(String arguments) {
        commandHandler.reorder(arguments);
    }
}
