package Pokemons;
import Moves.*;
import ru.ifmo.se.pokemon.*;
public class Glaceon extends Eevee{
    public Glaceon(String name, int level){
        super(name, level);
        setType(Type.ICE);
        setStats(65, 60, 110, 130, 95, 65);
        addMove(new IceFang());
    }
}