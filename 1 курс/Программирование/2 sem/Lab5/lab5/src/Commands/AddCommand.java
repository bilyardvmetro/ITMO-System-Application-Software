package Commands;

import Modules.CommandHandler;
import Modules.ConsoleApp;

public class AddCommand implements Command {
    private CommandHandler commandHandler;

    public AddCommand(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
        ConsoleApp.commandList.put("add", this);
    }

    @Override
    public void execute(String arguments) {
        commandHandler.add(arguments);
    }
}
