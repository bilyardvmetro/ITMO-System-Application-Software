package Classes.StellarBodies;

import Interfaces.IViewable;

public class NightSky implements IViewable {
    protected String name;
    public NightSky(){
        setName("ночное небо");
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
