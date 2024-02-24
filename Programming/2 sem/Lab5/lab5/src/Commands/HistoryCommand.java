package Commands;

import Modules.CommandHandler;

public class HistoryCommand implements Command{
    private CommandHandler commandHandler;

    public HistoryCommand(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
        CommandHandler.commandList.put("history", this);
    }

    @Override
    public void execute(String arguments) {
        commandHandler.history(arguments);
    }
}
