package Entities.Inanimate;

import AbstractClasses.Person;

public class House {
    private String name;

    public House(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public static class Doorbell{
        private final String NAME;

        public Doorbell() {
            this.NAME = "Дверной звонок";
        }

        public String getName() {
            return NAME;
        }

        public void ring(Person who_rings){
            System.out.print("Дзынь-Дзынь! ");
            System.out.print(who_rings.getName() + " позвонил в дверь. ");
        }
    }
    public static class Door{
        private final String NAME;

        public Door() {
            this.NAME = "Дверь";
        }

        public String getName() {
            return NAME;
        }

        public void open(Person who_open){
            System.out.print(who_open.getName() + " открыл дверь. ");
        }
    }
}
