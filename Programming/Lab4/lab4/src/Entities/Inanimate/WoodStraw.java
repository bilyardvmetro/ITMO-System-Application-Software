package Entities.Inanimate;

import AbstractClasses.Item;
import AbstractClasses.Person;

public class WoodStraw extends Item {

    public WoodStraw(String name) {
        super(name);
    }

    @Override
    public void grab(Person someone) {
        System.out.print(someone.getName() + " взял деревянную трубочку");
    }
}
