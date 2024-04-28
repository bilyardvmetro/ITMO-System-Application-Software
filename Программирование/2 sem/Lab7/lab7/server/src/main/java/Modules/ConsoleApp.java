package Modules;

import CollectionObject.VehicleModel;
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
    private Command removeGreater;
    private Command reorder;
    private Command history;
    private Command removeAllByType;
    private Command countGreaterThanEnginePower;
    private Command filterStartsWithName;

    public ConsoleApp(Command help, Command info, Command show, Command add, Command update, Command removeById,
                      Command clear, Command removeGreater, Command reorder, Command history, Command removeAllByType,
                      Command countGreaterThanEnginePower, Command filterStartsWithName) {
        this.help = help;
        this.info = info;
        this.show = show;
        this.add = add;
        this.update = update;
        this.removeById = removeById;
        this.clear = clear;
        this.removeGreater = removeGreater;
        this.reorder = reorder;
        this.history = history;
        this.removeAllByType = removeAllByType;
        this.countGreaterThanEnginePower = countGreaterThanEnginePower;
        this.filterStartsWithName = filterStartsWithName;
    }

    public Response help(String arguments, VehicleModel objectArg){
        return help.execute(arguments, objectArg);
    }

    public Response info(String arguments, VehicleModel objectArg){
        return info.execute(arguments, objectArg);
    }

    public Response show(String arguments, VehicleModel objectArg){
        return show.execute(arguments, objectArg);
    }

    public Response add(String arguments, VehicleModel objectArg){
        return add.execute(arguments, objectArg);
    }

    public Response update(String arguments, VehicleModel objectArg){
        return update.execute(arguments, objectArg);
    }

    public Response removeById(String arguments, VehicleModel objectArg){
        return removeById.execute(arguments, objectArg);
    }

    public Response clear(String arguments, VehicleModel objectArg){
        return clear.execute(arguments, objectArg);
    }

    public Response removeGreater(String arguments, VehicleModel objectArg){
        return removeGreater.execute(arguments, objectArg);
    }

    public Response reorder(String arguments, VehicleModel objectArg){
        return reorder.execute(arguments, objectArg);
    }

    public Response history(String arguments, VehicleModel objectArg){
        return history.execute(arguments, objectArg);
    }

    public Response removeAllByType(String arguments, VehicleModel objectArg){
        return removeAllByType.execute(arguments, objectArg);
    }

    public Response countGreaterThanEnginePower(String arguments, VehicleModel objectArg){
        return countGreaterThanEnginePower.execute(arguments, objectArg);
    }

    public Response filterStartsWithName(String arguments, VehicleModel objectArg){
        return filterStartsWithName.execute(arguments, objectArg);
    }
}
