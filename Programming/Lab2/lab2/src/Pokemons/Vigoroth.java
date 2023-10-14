package Pokemons;
import ru.ifmo.se.pokemon.*;
import Moves.*;
public class Vigoroth extends  Slakoth{
    public Vigoroth(String name, int level){
        super(name, level);
        setType(Type.NORMAL);
        setStats(80, 80, 80, 55, 55, 90);
        addMove(new Slash());
    }
}
