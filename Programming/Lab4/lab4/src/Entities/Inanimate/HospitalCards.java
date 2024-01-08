package Entities.Inanimate;

import AbstractClasses.Item;
import AbstractClasses.Person;

public class HospitalCards extends Item {
    public HospitalCards(String name) {
        super(name);
    }

    @Override
    public void grab(Person someone) {
        System.out.println(someone.getName() + " взял больничные карты. ");
    }
}
