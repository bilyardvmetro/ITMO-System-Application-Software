package Pokemons;
import Moves.PhysicalMoves.ShadowClaw;
import Moves.StatusMoves.Swagger;
import ru.ifmo.se.pokemon.*;
public class Slakoth extends Pokemon {
    public Slakoth(String name, int level){
        super(name, level);
        if (level >= 1 && level <= 100) {
            setType(Type.NORMAL);
            setStats(60, 60, 60, 35, 35, 30);
            setMove(new Swagger(), new ShadowClaw());
        }
        else {
            System.out.println("Введите валидный уровень покемона");
            System.exit(1);
        }
    }
}
