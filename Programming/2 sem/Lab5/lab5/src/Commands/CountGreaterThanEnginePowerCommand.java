package Commands;

import Modules.CommandHandler;
import Modules.ConsoleApp;

public class CountGreaterThanEnginePowerCommand implements Command {
    private CommandHandler commandHandler;

    public CountGreaterThanEnginePowerCommand(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
        ConsoleApp.commandList.put("countGreaterThanEnginePower", this);
    }

    @Override
    public void execute(String arguments) {
        commandHandler.countGreaterThanEnginePower(arguments);
    }
}
