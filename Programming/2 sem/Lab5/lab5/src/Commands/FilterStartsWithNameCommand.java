package Commands;

import Modules.CommandHandler;

public class FilterStartsWithNameCommand implements Command{
    private CommandHandler commandHandler;

    public FilterStartsWithNameCommand(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
        CommandHandler.commandList.put("filterStartsWithName", this);
    }

    @Override
    public void execute(String arguments) {
        commandHandler.filterStartsWithName(arguments);
    }
}
