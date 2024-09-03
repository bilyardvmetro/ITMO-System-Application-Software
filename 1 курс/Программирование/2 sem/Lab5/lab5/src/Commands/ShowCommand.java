package Commands;

import Modules.CommandHandler;
import Modules.ConsoleApp;

public class ShowCommand implements Command{
    private CommandHandler commandHandler;

    public ShowCommand(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
        ConsoleApp.commandList.put("show", this);
    }

    @Override
    public void execute(String arguments) {
        commandHandler.show(arguments);
    }
}
