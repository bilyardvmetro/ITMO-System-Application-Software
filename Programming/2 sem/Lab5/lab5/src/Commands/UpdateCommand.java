package Commands;

import Modules.CommandHandler;

public class UpdateCommand implements Command{
    private CommandHandler commandHandler;

    public UpdateCommand(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
    }

    @Override
    public void execute() {
        commandHandler.update();
    }
}
