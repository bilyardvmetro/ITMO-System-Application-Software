package Entities.Alive;

import AbstractClasses.BodyPart;
import AbstractClasses.Person;
import Enums.Mood;

public class Snezhinka extends Person {
    public Snezhinka(String name) {
        super(name);
    }

    public void grab_smb(Person grab_who, BodyPart body_part) {
        System.out.print("Снежинка взяла " + grab_who.getName() + " за " + body_part.getName() + ". ");
    }

    public void snort(){
        System.out.print("Снежинка фыркнула. ");
    }

    public void look_on_smb(Person person){
        if (person instanceof Neznaika){
            setMood(Mood.DELIGHT);
            System.out.print("Снежинка с восторгом смотрела на Незнайку и ");
            surpise();
            System.out.print(" его находчивости. ");
        }
    }

    public void surpise(){
        setMood(Mood.SURPRISE);
        System.out.print("Снежинка удивлялась");
    }
}
