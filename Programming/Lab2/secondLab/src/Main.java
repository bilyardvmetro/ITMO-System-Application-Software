import Pokemons.*;
import ru.ifmo.se.pokemon.*;

public class Main {
    public static void main(String[] args) {
        Battle battle = new Battle();
        Pokemon Pachirisu = new Pachirisu("Пачирису", 1);
        Pokemon Eevee = new Eevee("Иви", 1);
        Pokemon Glaceon = new Glaceon("Глаcеон", 1);
        Pokemon Slakoth = new Slakoth("Слакот", 1);
        Pokemon Vigoroth = new Vigoroth("Вигорот", 1);
        Pokemon Slaking = new Slaking("Слакинг", 1);

        Pokemon[] pokemonList = {Pachirisu, Eevee, Glaceon, Slakoth, Vigoroth, Slaking};
        for (int i = 0; i < pokemonList.length; i++){
            if (i < pokemonList.length /2){
                battle.addAlly(pokemonList[i]);
            }
            else {
                battle.addFoe(pokemonList[i]);
            }
        }
        battle.go();
    }
}