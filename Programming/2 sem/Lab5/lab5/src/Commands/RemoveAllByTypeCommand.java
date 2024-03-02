package Commands;

import Modules.CommandHandler;
import Modules.ConsoleApp;

public class RemoveAllByTypeCommand implements Command{
    private CommandHandler commandHandler;

    public RemoveAllByTypeCommand(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
        ConsoleApp.commandList.put("removeAllByType", this);
    }

    @Override
    public void execute(String arguments) {
        commandHandler.removeAllByType(arguments);
    }
}
