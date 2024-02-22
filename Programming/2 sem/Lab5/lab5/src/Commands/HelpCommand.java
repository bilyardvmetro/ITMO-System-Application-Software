package Commands;
import Modules.CommandHandler;

public class HelpCommand implements Command {
    CommandHandler commandHandler;

    public HelpCommand(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
    }

    @Override
    public void execute(String arguments) {
        commandHandler.help(arguments);
    }
}