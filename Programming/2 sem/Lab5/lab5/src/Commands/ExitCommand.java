package Commands;

import Modules.CommandHandler;

public class ExitCommand implements Command{
    private CommandHandler commandHandler;

    public ExitCommand(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
        CommandHandler.commandList.put("exit", this);
    }

    @Override
    public void execute(String arguments) {
        commandHandler.exit(arguments);
    }
}
