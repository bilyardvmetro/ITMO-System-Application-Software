package Commands;

import Modules.CommandHandler;

public class SaveCommand implements Command{
    private CommandHandler commandHandler;

    public SaveCommand(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
        CommandHandler.commandList.put("save", this);
    }

    @Override
    public void execute(String arguments) {
        commandHandler.save(arguments);
    }
}
