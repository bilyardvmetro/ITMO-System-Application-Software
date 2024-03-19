package Classes.StellarBodies;

import AbstractClasses.StellarBody;
import Classes.Entities.Planet;
import Enums.StellarBodyType;
import Interfaces.IViewable;

public class Moon extends StellarBody implements IViewable {
    public Moon(){
        super("Луна", StellarBodyType.MOON);
    }

    public String hasView(){
        return "какой вид имеет сама " + getName();
    }
}
