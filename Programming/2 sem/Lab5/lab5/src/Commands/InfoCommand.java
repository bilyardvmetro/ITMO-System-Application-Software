package Commands;

import Modules.CommandHandler;

public class InfoCommand implements Command{
    private CommandHandler commandHandler;

    public InfoCommand(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
    }

    @Override
    public void execute(String arguments) {
        commandHandler.info(arguments);
    }
}
