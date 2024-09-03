package Classes.Entities;

import AbstractClasses.StellarBody;
import Enums.StellarBodyType;
import Interfaces.ICallable;
import Interfaces.IViewable;

public class Planet extends StellarBody implements ICallable {
    public Planet() {
        super("планета", StellarBodyType.PLANET);
    }

    public String call(){
        return ", которая называется ";
    }
}
