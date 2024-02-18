package Commands;

import Modules.CommandHandler;

public class ClearCommand implements Command{
    private CommandHandler commandHandler;

    public ClearCommand(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
    }

    @Override
    public void execute(String arguments) {
        commandHandler.clear(arguments);
    }
}
