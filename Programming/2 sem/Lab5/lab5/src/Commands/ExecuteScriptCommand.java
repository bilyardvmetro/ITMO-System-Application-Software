package Commands;

import Modules.CommandHandler;
import Modules.ConsoleApp;

public class ExecuteScriptCommand implements Command{
    private CommandHandler commandHandler;

    public ExecuteScriptCommand(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
        ConsoleApp.commandList.put("executeScript", this);
    }

    @Override
    public void execute(String arguments) {
        commandHandler.executeScript(arguments);
    }
}
