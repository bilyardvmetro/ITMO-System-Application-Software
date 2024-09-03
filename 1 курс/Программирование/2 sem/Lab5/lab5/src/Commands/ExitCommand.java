package Commands;

import Modules.CommandHandler;
import Modules.ConsoleApp;

public class ExitCommand implements Command{
    private CommandHandler commandHandler;

    public ExitCommand(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
        ConsoleApp.commandList.put("exit", this);
    }

    @Override
    public void execute(String arguments) {
        commandHandler.exit(arguments);
    }
}
