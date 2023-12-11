import AbstractClasses.Person;
import Classes.Entities.Conversation;
import Classes.Entities.EarthShell;
import Classes.Entities.Planet;
import Classes.Entities.SpaceConference;
import Classes.Groups.EarthInhabitants;
import Classes.Groups.GroupOfScientists;
import Classes.Groups.LunarAstronomers;
import Classes.Persons.Alpha;
import Classes.Persons.Memega;
import Classes.Persons.Neznaika;
import Classes.StellarBodies.*;
import Enums.Location;

public class Main {
    public static void main(String[] args) {
        // Первое предложение
        LunarAstronomers lunarAstronomers = new LunarAstronomers();
        BigEarth bigEarth = new BigEarth();
        SmallEarth smallEarth = new SmallEarth();
        Earth earth = new Earth();
        Planet planet = new Planet();

        System.out.println("Известно, что " + lunarAstronomers.call() + " нашу " + planet.getName() + " " + bigEarth.getName()
        + " в отличие от своей собственной " + planet.getName() + ", которая" + smallEarth.call() + " или просто "
                + earth.getName() + ". ");

        // Второе предложение
        Neznaika neznaika = new Neznaika();
        EarthInhabitants obitateli = new EarthInhabitants();
        EarthShell earthShell = new EarthShell();

        System.out.println(lunarAstronomers.ask() + ", имеется ли" + bigEarth.getLocationInfo(Location.AROUND)
                + " " + earthShell.getName() + ", и" + lunarAstronomers.surprise() + ", когда " + neznaika.say()
                + ", что" + earth.getLocationInfo(Location.AROUND) + " никакой " + earthShell.getName() + " нет и что "
                + obitateli.liveSomewhere(Location.UNDER_THE_OPEN_SPACE) + ".");

        // Третье предложение
        SpaceConference spaceConference = new SpaceConference();
        Conversation conversation = new Conversation();
        Alpha alpha = new Alpha();
        Memega memega = new Memega();

        System.out.println(spaceConference.endUp() + ", а на другой день" + conversation.organize(neznaika)
                + " с двумя учёными, один из которых" + alpha.sayType() + " -" + alpha.call() + ", - другой"
                + memega.sayType() + " -" + memega.call() + ".");

        // Четвёртое предложение
        GroupOfScientists alphaAndMemega = new GroupOfScientists(alpha, memega);
        NightSky nightSky = new NightSky();
        StarsAndConstellations starsAndConstellations = new StarsAndConstellations();
        Sun sun = new Sun();
        Moon moon = new Moon();
        System.out.println(alphaAndMemega.askSomebody(neznaika) + " о том, " + nightSky.hasView()
                + ", если на него глядеть с " + earth.getName() + ", " + starsAndConstellations.hasView() + ", а также "
                + planet.getName() + ", " + sun.hasView() + " и " + moon.hasView() + ".");
    }
}
