package Commands;

import Modules.CommandHandler;

public class SaveCommand implements Command{
    private CommandHandler commandHandler;

    public SaveCommand(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
    }

    @Override
    public void execute() {
        commandHandler.save();
    }
}
