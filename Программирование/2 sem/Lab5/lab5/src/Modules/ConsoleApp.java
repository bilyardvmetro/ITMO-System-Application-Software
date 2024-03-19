package Modules;

import Commands.Command;

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

    public void help(String arguments){
        help.execute(arguments);
    }

    public void info(String arguments){
        info.execute(arguments);
    }

    public void show(String arguments){
        show.execute(arguments);
    }

    public void add(String arguments){
        add.execute(arguments);
    }

    public void update(String arguments){
        update.execute(arguments);
    }

    public void removeById(String arguments){
        removeById.execute(arguments);
    }

    public void clear(String arguments){
        clear.execute(arguments);
    }

    public void save(String arguments){
        save.execute(arguments);
    }

    public void executeScript(String arguments){
        executeScript.execute(arguments);
    }

    public void removeGreater(String arguments){
        removeGreater.execute(arguments);
    }

    public void reorder(String arguments){
        reorder.execute(arguments);
    }

    public void history(String arguments){
        history.execute(arguments);
    }

    public void removeAllByType(String arguments){
        removeAllByType.execute(arguments);
    }

    public void countGreaterThanEnginePower(String arguments){
        countGreaterThanEnginePower.execute(arguments);
    }

    public void filterStartsWithName(String arguments){
        filterStartsWithName.execute(arguments);
    }

    public void exit(String arguments){
        exit.execute(arguments);
    }
}
