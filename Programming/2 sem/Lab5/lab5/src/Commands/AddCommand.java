package Commands;

import Modules.CommandHandler;

public class AddCommand implements Command {
    private CommandHandler commandHandler;

    public AddCommand(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
    }

    @Override
    public void execute() {
        commandHandler.add();
    }
}
