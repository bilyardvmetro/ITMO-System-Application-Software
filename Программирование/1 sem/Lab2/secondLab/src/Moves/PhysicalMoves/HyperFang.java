package Moves.PhysicalMoves;
import ru.ifmo.se.pokemon.*;
public class HyperFang extends  PhysicalMove{
    public HyperFang(){
        super(Type.NORMAL, 80, 90);
    }
    private boolean flag = false;
    @Override
    protected void applyOppEffects(Pokemon p){
        int chance = (int)(Math.random() * 101);
        if (chance <= 10){
            flag = true;
            Effect.flinch(p);
        }
    }
    @Override
    protected String describe(){
        if (flag) return "повезло! Покемон использует атаку Hyper Fang, наносит урон и вызывает страх у врага";
        else return "использует атаку Hyper Fang и наносит урон";
    }
}
