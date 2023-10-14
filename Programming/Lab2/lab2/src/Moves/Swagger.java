package Moves;
import ru.ifmo.se.pokemon.*;
public class Swagger extends StatusMove {
    public Swagger(){
        super(Type.NORMAL, 0, 85);
    }
    @Override
    protected void applyOppEffects(Pokemon p){
            Effect.confuse(p);
            p.setMod(Stat.ATTACK, +2);
    }
    @Override
    protected String describe(){
        return "использует атаку Swagger: вызывает растерянность и увеличивает атаку соперника на 2";
    }
}
