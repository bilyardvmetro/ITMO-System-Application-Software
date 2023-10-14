package Pokemons;
import Moves.*;
import ru.ifmo.se.pokemon.*;
public class Eevee extends Pokemon {
    public Eevee(String name, int level){
        super(name, level);
        setType(Type.NORMAL);
        setStats(55, 55, 50, 45, 65, 55);
        setMove(new Swagger(), new Confide(), new Tackle());
    }
}
