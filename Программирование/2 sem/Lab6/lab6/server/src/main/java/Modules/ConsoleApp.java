package Modules;

import CollectionObject.Vehicle;
import Commands.Command;
import Network.Response;

import java.util.HashMap;

public class ConsoleApp {
    // хэшмапа команд. Ключ - имя команды; Значение - класс-оболочка команды
    public static HashMap<String, Command> commandList = new HashMap<>();
    private Command help;
    private Command info;
    private Command show;
    private Command add;
    private Command update;
    private Command removeById;
    private Command clear;
    private Command save;
    private Command executeScript;
    private Command exit;
    private Command removeGreater;
    private Command reorder;
    private Command history;
    private Command removeAllByType;
    private Command countGreaterThanEnginePower;
    private Command filterStartsWithName;

    public ConsoleApp(Command help, Command info, Command show, Command add, Command update, Command removeById,
                      Command clear, Command save, Command executeScript, Command exit, Command removeGreater,
                      Command reorder, Command history, Command removeAllByType, Command countGreaterThanEnginePower,
                      Command filterStartsWithName) {
        this.help = help;
        this.info = info;
        this.show = show;
        this.add = add;
        this.update = update;
        this.removeById = removeById;
        this.clear = clear;
        this.save = save;
        this.executeScript = executeScript;
        this.exit = exit;
        this.removeGreater = removeGreater;
        this.reorder = reorder;
        this.history = history;
        this.removeAllByType = removeAllByType;
        this.countGreaterThanEnginePower = countGreaterThanEnginePower;
        this.filterStartsWithName = filterStartsWithName;
    }

    public Response help(String arguments, Vehicle objectArg){
        return help.execute(arguments, objectArg);
    }

    public Response info(String arguments, Vehicle objectArg){
        return info.execute(arguments, objectArg);
    }

    public Response show(String arguments, Vehicle objectArg){
        return show.execute(arguments, objectArg);
    }

    public Response add(String arguments, Vehicle objectArg){
        return add.execute(arguments, objectArg);
    }

    public Response update(String arguments, Vehicle objectArg){
        return update.execute(arguments, objectArg);
    }

    public Response removeById(String arguments, Vehicle objectArg){
        return removeById.execute(arguments, objectArg);
    }

    public Response clear(String arguments, Vehicle objectArg){
        return clear.execute(arguments, objectArg);
    }

    public Response save(String arguments, Vehicle objectArg){
        return save.execute(arguments, objectArg);
    }

    public Response executeScript(String arguments, Vehicle objectArg){
        return executeScript.execute(arguments, objectArg);
    }

    public Response removeGreater(String arguments, Vehicle objectArg){
        return removeGreater.execute(arguments, objectArg);
    }

    public Response reorder(String arguments, Vehicle objectArg){
        return reorder.execute(arguments, objectArg);
    }

    public Response history(String arguments, Vehicle objectArg){
        return history.execute(arguments, objectArg);
    }

    public Response removeAllByType(String arguments, Vehicle objectArg){
        return removeAllByType.execute(arguments, objectArg);
    }

    public Response countGreaterThanEnginePower(String arguments, Vehicle objectArg){
        return countGreaterThanEnginePower.execute(arguments, objectArg);
    }

    public Response filterStartsWithName(String arguments, Vehicle objectArg){
        return filterStartsWithName.execute(arguments, objectArg);
    }

    public Response exit(String arguments, Vehicle objectArg){
        return exit.execute(arguments, objectArg);
    }
}
