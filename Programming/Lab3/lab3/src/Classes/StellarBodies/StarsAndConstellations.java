package Classes.StellarBodies;

import AbstractClasses.StellarBody;
import Enums.StellarBodyType;
import Interfaces.IViewable;

public class StarsAndConstellations extends StellarBody implements IViewable {
    public StarsAndConstellations(){
        super("звёзды и созвездия", StellarBodyType.STARS_AND_CONSTELLATIONS);
    }

    public String hasView(){
        return "какие на нём видны отдельные " + getName();
    }
}
