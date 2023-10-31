package Pokemons;
import Moves.PhysicalMoves.Slash;
import ru.ifmo.se.pokemon.*;

public class Vigoroth extends  Slakoth{
    public Vigoroth(String name, int level){
        super(name, level);
        setType(Type.NORMAL);
        setStats(80, 80, 80, 55, 55, 90);
        addMove(new Slash());
    }
}
