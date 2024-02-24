package Commands;

import Modules.CommandHandler;

public class InfoCommand implements Command{
    private CommandHandler commandHandler;

    public InfoCommand(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
        CommandHandler.commandList.put("info", this);
    }

    @Override
    public void execute(String arguments) {
        commandHandler.info(arguments);
    }
}
