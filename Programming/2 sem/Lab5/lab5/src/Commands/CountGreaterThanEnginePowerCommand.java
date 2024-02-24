package Commands;

import Modules.CommandHandler;

public class CountGreaterThanEnginePowerCommand implements Command {
    private CommandHandler commandHandler;

    public CountGreaterThanEnginePowerCommand(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
        CommandHandler.commandList.put("countGreaterThanEnginePower", this);
    }

    @Override
    public void execute(String arguments) {
        commandHandler.countGreaterThanEnginePower(arguments);
    }
}
