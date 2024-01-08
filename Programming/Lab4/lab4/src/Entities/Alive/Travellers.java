package Entities.Alive;

import AbstractClasses.Person;
import Enums.Location;

import java.util.LinkedList;

public final class Travellers {
    public LinkedList<Person> travellers;
    private String name;
    private Location location;
    {
        name = "Путешественники";
        location = Location.START_LOCATION;
    }

    public Travellers() {
        travellers = new LinkedList<>();
    }

    public void getTravellers() {
        class ToStringConverter {
            private LinkedList<Person> convertable;
            public ToStringConverter(LinkedList<Person> convertable) {
                this.convertable = convertable;
            }
            public String convert(){
                StringBuilder s = new StringBuilder("Путешественники:");
                for (Person item : convertable){
                    s.append(" ").append(item.getName());
                }
                return s.toString();
            }
        }
        ToStringConverter converter = new ToStringConverter(travellers);
        System.out.println(converter.convert());
    }

    public void addTraveller(Person person) {
        this.travellers.add(person);
    }

    public void removeTraveller(Person person){
        this.travellers.remove(person);
    }

    public void move(Location where) {
        System.out.print("Путешественники вышли из локации " + location.getTitle() + " и перешли в локацию " + where.getTitle() + ". ");
        for (Person traveller : travellers) {
            traveller.setLocation(where);
        }
        this.location = where;
    }

    public void setLocation(Location where) {
        this.location = where;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Location getLocation() {
        return location;
    }
}
