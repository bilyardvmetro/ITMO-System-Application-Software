package Classes.Persons;

import AbstractClasses.Person;
import Enums.ScientistType;
import Interfaces.ICallable;
import Interfaces.ISayType;

public class Alpha extends Person implements ICallable, ISayType {
    protected ScientistType type;

    {
        type = ScientistType.ASTRONOMER;
    }

    public Alpha(){
        super("Альфа");
    }

    public String sayType(){
        return " был астрономом";
    }

    public String call(){
        return " его звали " + this.getName();
    }
}
