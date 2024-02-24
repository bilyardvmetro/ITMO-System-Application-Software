package Commands;

import Modules.CommandHandler;

public class ExecuteScriptCommand implements Command{
    private CommandHandler commandHandler;

    public ExecuteScriptCommand(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
        CommandHandler.commandList.put("executeScript", this);
    }

    @Override
    public void execute(String arguments) {
        commandHandler.executeScript(arguments);
    }
}
