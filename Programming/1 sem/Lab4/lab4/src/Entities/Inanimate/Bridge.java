package Entities.Inanimate;

import AbstractClasses.Person;
import Entities.Alive.Neznaika;
import Interfaces.Grabable;

public class Bridge implements Grabable {
    private final String NAME = "мост";
    private Person crosser;

    public Bridge() {}

    public String getName() {
        return NAME;
    }

    public Person getCrosser() {
        return crosser;
    }

    public void setCrosser(Person crosser) {
        this.crosser = crosser;
    }

    public void swing(){
        System.out.print("Мост начал качаться. ");
        if (crosser instanceof Neznaika){
            ((Neznaika) crosser).stop();
            ((Neznaika) crosser).fear();
            ((Neznaika) crosser).grapple(this);
        }
    }

    @Override
    public void grab(Person someone) {
        System.out.print(crosser.getName() + " ухватился за мост. ");
    }
}
