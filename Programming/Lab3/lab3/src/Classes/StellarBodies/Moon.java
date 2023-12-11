package Classes.StellarBodies;

import Classes.Entities.Planet;
import Interfaces.IViewable;

public class Moon extends Planet implements IViewable {
    protected String name;
    public Moon(){
        setName("Луна");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String hasView(){
        return "какой вид имеет сама " + getName();
    }
}
