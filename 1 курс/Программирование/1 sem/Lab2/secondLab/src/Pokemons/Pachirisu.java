package Pokemons;
import Moves.PhysicalMoves.Facade;
import Moves.PhysicalMoves.HyperFang;
import Moves.StatusMoves.Confide;
import Moves.StatusMoves.Swagger;
import ru.ifmo.se.pokemon.*;
public class Pachirisu extends Pokemon{
    public Pachirisu(String name, int level){
        super(name, level);
        if (level >= 1 && level <= 100) {
            setType(Type.ELECTRIC);
            setStats(60, 45, 70, 45, 90, 95);
            setMove(new Confide(), new Swagger(), new HyperFang(), new Facade());
        }
        else {
            System.out.println("Введите валидный уровень покемона");
            System.exit(1);
        }
    }
}