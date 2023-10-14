package Moves;
import ru.ifmo.se.pokemon.*;
public class IceFang extends PhysicalMove{
    public IceFang(){
        super(Type.ICE,65, 95);
    }
    private boolean freezeFlag = false;
    private boolean flinchFlag = false;
    @Override
    protected void applyOppEffects(Pokemon p){
        int freezeChance = (int)(Math.random()*101);
        int flinchChance = (int)(Math.random()*101);
        if (!p.hasType(Type.ICE) && freezeChance <= 10){
            freezeFlag = true;
            Effect.freeze(p);
        }
        if (flinchChance <= 10) {
            flinchFlag = true;
            Effect.flinch(p);
        }
    }
    @Override
    protected String describe(){
        if (freezeFlag & flinchFlag) return "повезло! Покемон использует атаку Ice Fang и наносит урон, замораживая врага и вгоняя его в страх";
        if (freezeFlag) return "повезло! Покемон использует атаку Ice Fang, нанося урон и замораживая врага";
        if (flinchFlag) return "повезло! Покемон использует атаку Ice Fang, нанося урон и вгоняя врага в страх";
        else return "Использует атаку Ice Fang, нанося урон";
    }
}
