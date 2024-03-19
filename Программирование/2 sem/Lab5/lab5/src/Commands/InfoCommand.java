package Commands;

import Modules.CommandHandler;
import Modules.ConsoleApp;

public class InfoCommand implements Command{
    private CommandHandler commandHandler;

    public InfoCommand(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
        ConsoleApp.commandList.put("info", this);
    }

    @Override
    public void execute(String arguments) {
        commandHandler.info(arguments);
    }
}
