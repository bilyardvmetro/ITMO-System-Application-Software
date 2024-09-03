package Commands;

import Modules.CommandHandler;
import Modules.ConsoleApp;

public class RemoveByIdCommand implements Command{
    private CommandHandler commandHandler;

    public RemoveByIdCommand(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
        ConsoleApp.commandList.put("removeById", this);
    }

    @Override
    public void execute(String arguments) {
        commandHandler.removeById(arguments);
    }
}
