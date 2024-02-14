package Commands;

import Modules.CommandHandler;

public class RemoveByIdCommand implements Command{
    private CommandHandler commandHandler;

    public RemoveByIdCommand(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
    }

    @Override
    public void execute() {
        commandHandler.removeById();
    }
}
