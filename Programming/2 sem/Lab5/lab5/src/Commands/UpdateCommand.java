package Commands;

import Modules.CommandHandler;
import Modules.ConsoleApp;

public class UpdateCommand implements Command{
    private CommandHandler commandHandler;

    public UpdateCommand(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
        ConsoleApp.commandList.put("update", this);
    }

    @Override
    public void execute(String arguments) {
        commandHandler.update(arguments);
    }
}
