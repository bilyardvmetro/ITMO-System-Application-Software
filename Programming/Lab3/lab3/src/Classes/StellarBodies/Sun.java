package Classes.StellarBodies;

import AbstractClasses.StellarBody;
import Classes.Entities.Planet;
import Enums.StellarBodyType;
import Interfaces.IViewable;

public class Sun extends StellarBody implements IViewable {
    public Sun(){
        super("Солнце", StellarBodyType.SUN);
    }
    public String hasView(){
        return "какой вид имеет " + getName();
    }
}
