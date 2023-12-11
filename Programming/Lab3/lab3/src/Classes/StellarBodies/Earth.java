package Classes.StellarBodies;

import AbstractClasses.StellarBody;
import Enums.EarthSize;
import Enums.Location;
import Enums.StellarBodyType;
import Interfaces.IViewable;

public class Earth extends StellarBody {
    protected EarthSize size;
    public Earth() {
        super("Земля", StellarBodyType.EARTH);
        this.setSize(EarthSize.DEFAULT);
    }

    public Earth(String name, StellarBodyType type) {
        super(name, type);
    }

    public void setSize(EarthSize size) {
        this.size = size;
    }

    public String getLocationInfo(Location location) {
        if (location == Location.AROUND){
                return " вокруг нашей " + this.getName();
            }
        else {
            return "";
        }
    }
}
