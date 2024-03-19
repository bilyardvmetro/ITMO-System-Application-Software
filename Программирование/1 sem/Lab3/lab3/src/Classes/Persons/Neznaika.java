package Classes.Persons;

import AbstractClasses.Person;

public class Neznaika extends Person {
    public Neznaika(){
        super("Незнайка");
    }
    public String say(){
        return this.getName() + " сказал";
    }
}
