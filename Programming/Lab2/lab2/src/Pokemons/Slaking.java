package Pokemons;
import Moves.*;
import ru.ifmo.se.pokemon.*;
public class Slaking extends Vigoroth{
    public Slaking(String name, int level){
        super(name, level);
        setType(Type.NORMAL);
        setStats(150, 160, 100, 95, 65, 100);
        addMove(new Swagger());
    }
}
