package Moves;
import ru.ifmo.se.pokemon.*;
public class Confide extends StatusMove {
    public Confide() {
        super(Type.NORMAL, 0, 100);
    }
    @Override
    protected void applyOppEffects(Pokemon p){
        p.setMod(Stat.SPECIAL_ATTACK,-1);
    }
    @Override
    protected boolean checkAccuracy(Pokemon p1, Pokemon p2){
        return true;
    }
    @Override
    protected String describe(){
        return "использует атаку Confide и уменьшает специальную атаку на 1";
    }
}
