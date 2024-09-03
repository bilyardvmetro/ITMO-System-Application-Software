package Commands;

import Modules.CommandHandler;
import Modules.ConsoleApp;

public class SaveCommand implements Command{
    private CommandHandler commandHandler;

    public SaveCommand(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
        ConsoleApp.commandList.put("save", this);
    }

    @Override
    public void execute(String arguments) {
        commandHandler.save(arguments);
    }
}
