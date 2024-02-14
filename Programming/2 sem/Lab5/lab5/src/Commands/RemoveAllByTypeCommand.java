package Commands;

import Modules.CommandHandler;

public class RemoveAllByTypeCommand implements Command{
    private CommandHandler commandHandler;

    public RemoveAllByTypeCommand(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
    }

    @Override
    public void execute() {
        commandHandler.removeAllByType();
    }
}
