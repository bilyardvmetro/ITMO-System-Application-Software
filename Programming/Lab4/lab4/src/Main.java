import Entities.Alive.*;
import Entities.Inanimate.*;
import Enums.Location;
import Interfaces.SentenceHandler;

public class Main {
    public static void main(String[] args) {
        Neznaika neznaika = new Neznaika("Незнайка");
        Sineglazka sineglazka = new Sineglazka("Синеглазка");
        Snezhinka snezhinka = new Snezhinka("Снежинка");
        Vorchun vorchun = new Vorchun("Ворчун");
        Nanny nanny = new Nanny("Нянечка");
        Medunitsa medunitsa = new Medunitsa("Медуница");
        Travellers travellers = new Travellers();
        travellers.addTraveller(neznaika);
        travellers.addTraveller(sineglazka);
        travellers.addTraveller(snezhinka);

        Bridge bridge = new Bridge();
        House house = new House("Белый дом с зелёной крышей");
        House.Doorbell doorbell = new House.Doorbell();
        House.Door door = new House.Door();
        Hanger.Cap kolpak = new Hanger.Cap("колпак");
        Hanger.LabCoat halat = new Hanger.LabCoat("лабораторный халат");
        Glasses glasses = new Glasses("очки");
        WoodStraw woodStraw = new WoodStraw("деревянная трубочка");
        HospitalCards hospitalCards = new HospitalCards("медицинские карточки");
        Table table = new Table();
        table.addItem(glasses);
        table.addItem(woodStraw);
        table.addItem(hospitalCards);

        snezhinka.snort();
        travellers.move(Location.RIVER_SHORE);
        SentenceMaker thirdSentence = new SentenceMaker(new SentenceHandler() {
            @Override
            public void sentenceOut() {
                System.out.print("Через реку вёл узенький мостик. ");
            }
        });
        thirdSentence.printSentence();
        SentenceMaker fourthSentence = new SentenceMaker(new SentenceHandler() {
            @Override
            public void sentenceOut() {
                System.out.print("Он был сделан из толстой и прочной материи. ");
            }
        });
        fourthSentence.printSentence();

        sineglazka.move(Location.BRIDGE);
        neznaika.move(Location.BRIDGE);
        travellers.setLocation(Location.BRIDGE);
        bridge.setCrosser(neznaika);
        bridge.swing();
        snezhinka.grab_smb(neznaika, neznaika.leftArm);
        sineglazka.grab_smb(neznaika, neznaika.rightArm);

        travellers.move(Location.ANOTHER_RIVER_SHORE);
        travellers.move(Location.STREET);
        travellers.move(Location.HOUSE);

        doorbell.ring(snezhinka);
        door.open(nanny);
        SentenceMaker nannyDescription = new SentenceMaker(new SentenceHandler() {
            @Override
            public void sentenceOut() {
                System.out.print("Нянечка была одета в белый халат и косыночку," +
                        " из-под которой выбивались золотистые локоны. ");
            }
        });
        nannyDescription.printSentence();
        travellers.move(Location.NURSE_OFFICE);
        medunitsa.write();
        table.printItems();
        medunitsa.conduct_medical_check_up(sineglazka);
        medunitsa.move(Location.UNNOWN_LOCATION);
        sineglazka.move(Location.UNNOWN_LOCATION);

        neznaika.put_on_item(halat, neznaika.body);
        neznaika.put_on_item(kolpak, neznaika.head);
        neznaika.put_on_item(glasses, neznaika.nose);
        snezhinka.look_on_smb(neznaika);

        neznaika.move(Location.HALLWAY);
        door.open(neznaika);
        neznaika.move(Location.HOSPITAL_ROOM);
        neznaika.move(Location.VORCHUNS_BUNK);
        vorchun.printMoodStatus();
    }
}
