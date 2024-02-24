package Commands;

import Modules.CommandHandler;

public class ShowCommand implements Command{
    private CommandHandler commandHandler;

    public ShowCommand(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
        CommandHandler.commandList.put("show", this);
    }

    @Override
    public void execute(String arguments) {
        commandHandler.show(arguments);
    }
}
