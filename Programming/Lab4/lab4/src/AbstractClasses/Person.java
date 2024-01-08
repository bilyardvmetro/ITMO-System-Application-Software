package AbstractClasses;

import Enums.Location;
import Enums.Mood;

public abstract class Person {
    private String name;
    private Location location;
    private Mood mood;
    {
        location = Location.START_LOCATION;
    }

    public Person(String name) {
        this.name = name;
    }

    public void move(Location where) {
        System.out.print(this.getName() + " вышел из локации " + this.getLocationTitle() + " и перешёл в локацию " + where.getTitle() + ". ");
        this.setLocation(where);
    }

    public void printMoodStatus() {
        switch (mood){
            case FEAR:
                System.out.print(this.getName() + " был напуган. ");
                break;
            case DELIGHT:
                System.out.print(this.getName() + " был восхищён. ");
                break;
            case SURPRISE:
                System.out.print(this.getName() + " был удивлён. ");
                break;
            case DISPLEASED_AND_GLOOMY:
                System.out.print(this.getName() + " был угрюм и недоволен. ");
                break;
        }
    }

    public String getLocationTitle() {
        return location.getTitle();
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Mood getMood() {
        return mood;
    }

    public void setMood(Mood mood) {
        this.mood = mood;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
