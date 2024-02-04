package Moves.PhysicalMoves;
import ru.ifmo.se.pokemon.*;
public class Slash extends PhysicalMove{
    public Slash(){
        super(Type.NORMAL, 70, 100);
    }
    @Override
    protected boolean checkAccuracy(Pokemon p1, Pokemon p2){
        return true;
    }
    @Override
    protected double calcCriticalHit(Pokemon att, Pokemon def){
        return (att.getStat(Stat.SPEED) / 512) + 1;
    }
    @Override
    protected String describe(){
        return "использует атаку Slash, нанося урон и увеличивая шанс критического удара на 1";
    }
}
