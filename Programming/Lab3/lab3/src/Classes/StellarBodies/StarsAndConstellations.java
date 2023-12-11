package Classes.StellarBodies;

import Interfaces.IViewable;

public class StarsAndConstellations implements IViewable {
    protected String name;
    public StarsAndConstellations(){
        setName("звёзды и созвездия");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String hasView(){
        return "какие на нём видны отдельные " + getName();
    }
}
