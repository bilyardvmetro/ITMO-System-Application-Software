package Pokemons;
import Moves.StatusMoves.Swagger;
import ru.ifmo.se.pokemon.*;
public class Slaking extends Vigoroth{
    public Slaking(String name, int level){
        super(name, level);
        if (level >= 1 && level <= 100) {
            setType(Type.NORMAL);
            setStats(150, 160, 100, 95, 65, 100);
            addMove(new Swagger());
        }
        else {
            System.out.println("Введите валидный уровень покемона");
            System.exit(1);
        }
    }
}
