package Commands;

import Modules.CommandHandler;
import Modules.ConsoleApp;

public class HistoryCommand implements Command{
    private CommandHandler commandHandler;

    public HistoryCommand(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
        ConsoleApp.commandList.put("history", this);
    }

    @Override
    public void execute(String arguments) {
        commandHandler.history(arguments);
    }
}
