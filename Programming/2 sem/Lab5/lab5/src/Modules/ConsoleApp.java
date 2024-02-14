package Modules;

import Commands.Command;

public class ConsoleApp {
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

    public void add(){
        add.execute();
    }

    public void help(){
        help.execute();
    }

    public void exit(){exit.execute();}
}
