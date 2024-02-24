package Commands;

import Modules.CommandHandler;

public class UpdateCommand implements Command{
    private CommandHandler commandHandler;

    public UpdateCommand(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
        CommandHandler.commandList.put("update", this);
    }

    @Override
    public void execute(String arguments) {
        commandHandler.update(arguments);
    }
}
