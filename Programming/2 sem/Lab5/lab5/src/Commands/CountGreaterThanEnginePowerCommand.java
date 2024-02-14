package Commands;

import Modules.CommandHandler;

public class CountGreaterThanEnginePowerCommand implements Command {
    private CommandHandler commandHandler;

    public CountGreaterThanEnginePowerCommand(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
    }

    @Override
    public void execute() {
        commandHandler.countGreaterThanEnginePower();
    }
}
