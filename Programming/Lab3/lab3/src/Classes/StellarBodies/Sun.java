package Classes.StellarBodies;

import Classes.Entities.Planet;
import Interfaces.IViewable;

public class Sun extends Planet implements IViewable {
    protected String name;
    public Sun(){
        setName("Солнце");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String hasView(){
        return "какой вид имеет " + getName();
    }
}
