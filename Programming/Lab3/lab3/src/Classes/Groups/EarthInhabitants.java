package Classes.Groups;

import AbstractClasses.Group;
import Enums.Location;

public class EarthInhabitants extends Group {
    public EarthInhabitants(){
        super("земные обитатели");
    }
    public String liveSomewhere(Location location) {
        if (location == Location.UNDER_THE_OPEN_SPACE) {
            return this.getGroup_name() + " живут, так сказать, под открытым космосом";
        }
        else {
            return "";
        }
    }
}
