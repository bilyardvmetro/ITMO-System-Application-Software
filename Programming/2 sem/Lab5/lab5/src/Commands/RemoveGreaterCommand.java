package Commands;

import Modules.CommandHandler;

public class RemoveGreaterCommand implements Command{
    private CommandHandler commandHandler;

    public RemoveGreaterCommand(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
    }

    @Override
    public void execute() {
        commandHandler.removeGreater();
    }
}
