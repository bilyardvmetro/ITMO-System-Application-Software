package Entities.Alive;

import AbstractClasses.Person;
import Enums.Location;

public class Medunitsa extends Person {
    public Medunitsa(String name) {
        super(name);
        this.setLocation(Location.NURSE_OFFICE);
    }

    public void write(){
        System.out.print("Медуница что-то писала. ");
    }

    public void conduct_medical_check_up(Person person){
        System.out.print("Медуница провела медицинский осмотр " + person.getName() + ". ");
    }
}
