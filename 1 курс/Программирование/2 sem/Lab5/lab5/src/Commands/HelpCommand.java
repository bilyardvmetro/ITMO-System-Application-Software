package Commands;
import Modules.CommandHandler;
import Modules.ConsoleApp;

public class HelpCommand implements Command {
    CommandHandler commandHandler;

    public HelpCommand(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
        ConsoleApp.commandList.put("help", this);
    }

    @Override
    public void execute(String arguments) {
        commandHandler.help(arguments);
    }
}
