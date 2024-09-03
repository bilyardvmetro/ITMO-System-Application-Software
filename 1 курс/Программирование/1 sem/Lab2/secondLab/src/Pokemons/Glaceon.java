package Pokemons;
import Moves.PhysicalMoves.IceFang;
import ru.ifmo.se.pokemon.*;
public class Glaceon extends Eevee{
    public Glaceon(String name, int level){
        super(name, level);
        if (level >= 1 && level <= 100) {
            setType(Type.ICE);
            setStats(65, 60, 110, 130, 95, 65);
            addMove(new IceFang());
        }
        else {
            System.out.println("Введите валидный уровень покемона");
            System.exit(1);
        }
    }
}