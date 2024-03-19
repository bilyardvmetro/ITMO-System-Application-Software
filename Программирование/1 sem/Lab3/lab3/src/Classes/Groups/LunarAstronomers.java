package Classes.Groups;

import AbstractClasses.Group;
import Enums.ScientistType;
import Interfaces.IAskable;
import Interfaces.ICallable;

public class LunarAstronomers extends Group implements ICallable, IAskable {
    protected ScientistType type;

    {
        type = ScientistType.LUNOLOGIST;
    }

    public LunarAstronomers(){
        super("Лунные астрономы");
    }
    public String call(){
        return this.getGroup_name() + " называют";
    }
    public String ask() {
        return (this.getGroup_name() + " очень интересовал вопрос");
    }
    public String surprise(){
        return " они были до крайности удивлены";
    }
}
