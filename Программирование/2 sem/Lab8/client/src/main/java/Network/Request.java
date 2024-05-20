package Network;

import java.io.Serializable;

public class Request implements Serializable {
    private User user;
    private boolean registerRequired;
    private String commandName;
    private String commandStrArg;
    private Serializable commandObjArg;

    public Request(User user, String commandName, String commandStrArg, Serializable commandObjArg) {
        this.user = user;
        this.commandName = commandName;
        this.commandStrArg = commandStrArg;
        this.commandObjArg = commandObjArg;
    }

    public Request(User user, String commandName, String commandStrArg) {
        this(user, commandName, commandStrArg, null);
    }

    public Request(User user, boolean registerRequired){
        this.user = user;
        this.registerRequired = registerRequired;
    }

    public String getCommandName() {
        return commandName;
    }

    public String getCommandStrArg() {
        return commandStrArg;
    }

    public Serializable getCommandObjArg() {
        return commandObjArg;
    }

    public User getUser() {
        return user;
    }

    public boolean userRegisterRequired() {
        return registerRequired;
    }

    @Override
    public String toString() {
        return "Request{" +
                "commandName='" + commandName + '\'' +
                ", commandStrArg='" + commandStrArg + '\'' +
                ", commandObjArg=" + commandObjArg +
                '}';
    }
}
