package Classes.Entities;

import AbstractClasses.StellarBody;
import Enums.StellarBodyType;
import Interfaces.ICallable;
import Interfaces.IViewable;

public class Planet extends StellarBody implements ICallable, IViewable {
    public Planet() {
        super("планета", StellarBodyType.PLANET);
    }

    public String call(){
        return ", которая называется ";
    }

    public String hasView(){
        return " какие на нём видны " + this.getName();
    }
}
