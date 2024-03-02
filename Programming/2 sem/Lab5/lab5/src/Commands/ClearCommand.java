package Commands;

import Modules.CommandHandler;
import Modules.ConsoleApp;

public class ClearCommand implements Command{
    private CommandHandler commandHandler;

    public ClearCommand(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
        ConsoleApp.commandList.put("clear", this);
    }

    @Override
    public void execute(String arguments) {
        commandHandler.clear(arguments);
    }
}
