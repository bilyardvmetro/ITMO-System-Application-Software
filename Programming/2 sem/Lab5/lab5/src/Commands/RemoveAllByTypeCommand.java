package Commands;

import Modules.CommandHandler;

public class RemoveAllByTypeCommand implements Command{
    private CommandHandler commandHandler;

    public RemoveAllByTypeCommand(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
        CommandHandler.commandList.put("removeAllByType", this);
    }

    @Override
    public void execute(String arguments) {
        commandHandler.removeAllByType(arguments);
    }
}
