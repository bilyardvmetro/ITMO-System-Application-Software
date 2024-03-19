package Entities.Alive;

import AbstractClasses.BodyPart;
import AbstractClasses.Person;

public class Sineglazka extends Person {
    public Sineglazka(String name) {
        super(name);
    }

    public void grab_smb(Person grab_who, BodyPart body_part) {
        System.out.print("Синеглазка взяла " + grab_who.getName() + " за " + body_part.getName() + ". ");
    }
}
