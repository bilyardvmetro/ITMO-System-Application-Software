package Classes.Persons;

import AbstractClasses.Person;
import Enums.ScientistType;
import Interfaces.ICallable;
import Interfaces.ISayType;

public class Memega extends Person implements ISayType, ICallable {
    protected ScientistType type;

    {
        type = ScientistType.LUNOLOGIST;
    }

    public Memega(){
        super("Мемега");
    }

    public String sayType(){
        return " был лунологом";
    }

    public String call(){
        return " его звали " + this.getName();
    }
}
