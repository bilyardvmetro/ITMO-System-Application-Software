package Modules;

import CollectionObject.VehicleModel;
import Commands.Command;
import Network.Response;
import Network.User;

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

    public Response help(User user, String arguments, VehicleModel objectArg) {
        return help.execute(user, arguments, objectArg);
    }

    public Response info(User user, String arguments, VehicleModel objectArg) {
        return info.execute(user, arguments, objectArg);
    }

    public Response show(User user, String arguments, VehicleModel objectArg) {
        return show.execute(user, arguments, objectArg);
    }

    public Response add(User user, String arguments, VehicleModel objectArg) {
        return add.execute(user, arguments, objectArg);
    }

    public Response update(User user, String arguments, VehicleModel objectArg) {
        return update.execute(user, arguments, objectArg);
    }

    public Response removeById(User user, String arguments, VehicleModel objectArg) {
        return removeById.execute(user, arguments, objectArg);
    }

    public Response clear(User user, String arguments, VehicleModel objectArg) {
        return clear.execute(user, arguments, objectArg);
    }

    public Response removeGreater(User user, String arguments, VehicleModel objectArg) {
        return removeGreater.execute(user, arguments, objectArg);
    }

    public Response reorder(User user, String arguments, VehicleModel objectArg) {
        return reorder.execute(user, arguments, objectArg);
    }

    public Response history(User user, String arguments, VehicleModel objectArg) {
        return history.execute(user, arguments, objectArg);
    }

    public Response removeAllByType(User user, String arguments, VehicleModel objectArg) {
        return removeAllByType.execute(user, arguments, objectArg);
    }

    public Response countGreaterThanEnginePower(User user, String arguments, VehicleModel objectArg) {
        return countGreaterThanEnginePower.execute(user, arguments, objectArg);
    }

    public Response filterStartsWithName(User user, String arguments, VehicleModel objectArg) {
        return filterStartsWithName.execute(user, arguments, objectArg);
    }
}
