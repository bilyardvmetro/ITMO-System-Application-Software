package Commands;

import Modules.CommandHandler;

public class ExitCommand implements Command{
    private CommandHandler commandHandler;

    public ExitCommand(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
    }

    @Override
    public void execute() {
        commandHandler.exit();
    }
}
