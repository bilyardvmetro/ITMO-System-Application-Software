package Entities.Inanimate;

import AbstractClasses.Clothing;
import AbstractClasses.Person;

public class Glasses extends Clothing{
    private Person wearer;

    public Glasses(String name) {
        super(name);
    }

    public Person getWearer() {
        return wearer;
    }

    public void setWearer(Person wearer) {
        this.wearer = wearer;
    }

    @Override
    public void grab(Person someone) {
        this.wearer = someone;
        System.out.print(someone.getName() + " взял очки. ");
    }
}
