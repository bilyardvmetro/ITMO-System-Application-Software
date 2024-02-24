package Commands;

import Modules.CommandHandler;

public class RemoveByIdCommand implements Command{
    private CommandHandler commandHandler;

    public RemoveByIdCommand(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
        CommandHandler.commandList.put("removeById", this);
    }

    @Override
    public void execute(String arguments) {
        commandHandler.removeById(arguments);
    }
}
