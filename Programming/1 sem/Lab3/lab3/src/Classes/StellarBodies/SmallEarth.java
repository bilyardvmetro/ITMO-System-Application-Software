package Classes.StellarBodies;

import Enums.EarthSize;
import Enums.StellarBodyType;
import Interfaces.ICallable;

public class SmallEarth extends Earth implements ICallable {
    public SmallEarth() {
        super("Малая Земля", StellarBodyType.EARTH);
        this.setSize(EarthSize.SMALL);
    }

    public String call(){
        return " называется " + this.getName();
    }
}
