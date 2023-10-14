package Pokemons;
import ru.ifmo.se.pokemon.*;
import Moves.*;
public class Slakoth extends Pokemon {
    public Slakoth(String name, int level){
        super(name, level);
        setType(Type.NORMAL);
        setStats(60, 60, 60, 35, 35, 30);
        setMove(new Swagger(), new ShadowClaw());
    }
}
