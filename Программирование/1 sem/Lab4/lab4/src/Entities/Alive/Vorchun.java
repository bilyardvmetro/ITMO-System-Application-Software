package Entities.Alive;

import AbstractClasses.Person;
import Enums.Location;
import Enums.Mood;

public class Vorchun extends Person {
    public Vorchun(String name) {
        super(name);
        this.setMood(Mood.DISPLEASED_AND_GLOOMY);
        this.setLocation(Location.VORCHUNS_BUNK);
    }
}
