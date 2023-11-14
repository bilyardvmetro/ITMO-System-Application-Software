package Pokemons;
import Moves.PhysicalMoves.Tackle;
import Moves.StatusMoves.Confide;
import Moves.StatusMoves.Swagger;
import ru.ifmo.se.pokemon.*;
public class Eevee extends Pokemon {
    public Eevee(String name, int level){
        super(name, level);
        if (level >= 1 && level <= 100) {
            setType(Type.NORMAL);
            setStats(55, 55, 50, 45, 65, 55);
            setMove(new Swagger(), new Confide(), new Tackle());
        }
        else {
            System.out.println("Введите валидный уровень покемона");
            System.exit(1);
        }
    }
}
