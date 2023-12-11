package Classes.StellarBodies;

import Enums.EarthSize;
import Enums.Location;
import Enums.StellarBodyType;

public class BigEarth extends Earth {
    public BigEarth() {
        super("Большая Земля", StellarBodyType.EARTH);
        this.setSize(EarthSize.BIG);
    }
    @Override
    public String getLocationInfo(Location location) {
        if (location == Location.AROUND){
            return " вокруг " + this.getName();
        }
        else {
            return "";
        }
    }
}
