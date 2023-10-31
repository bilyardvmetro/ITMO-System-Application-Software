package Moves.PhysicalMoves;
import ru.ifmo.se.pokemon.*;

public class Tackle extends PhysicalMove {
    public Tackle(){
        super(Type.NORMAL, 60, 100);
    }
    @Override
    protected boolean checkAccuracy(Pokemon p1, Pokemon p2){
        return true;
    }
    @Override
    protected String describe(){
        return "использует атаку Tackle, наносящую урон без дополнительных эффектов";
    }
}
