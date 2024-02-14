package Commands;

import Modules.CommandHandler;

public class ShowCommand implements Command{
    private CommandHandler commandHandler;

    public ShowCommand(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
    }

    @Override
    public void execute() {
        commandHandler.show();
    }
}
