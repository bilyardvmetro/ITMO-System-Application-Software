package Commands;

import Modules.CommandHandler;

public class ReorderCommand implements Command{
    private CommandHandler commandHandler;

    public ReorderCommand(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
    }

    @Override
    public void execute() {
        commandHandler.reorder();
    }
}
