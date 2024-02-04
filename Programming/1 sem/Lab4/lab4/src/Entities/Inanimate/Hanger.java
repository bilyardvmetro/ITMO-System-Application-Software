package Entities.Inanimate;

import AbstractClasses.Clothing;
import AbstractClasses.Person;

public class Hanger {
    public static class LabCoat extends Clothing {
        private Person wearer;

        public LabCoat(String name) {
            super(name);
        }

        public Person getWearer() {
            return wearer;
        }

        public void setWearer(Person wearer) {
            this.wearer = wearer;
        }

        @Override
        public void grab(Person someone) {
            this.wearer = someone;
            System.out.print(someone.getName() + " взял лабораторный халат. ");
        }
    }
    public static class Cap extends Clothing{
        private Person wearer;

        public Cap(String name) {
            super(name);
        }

        public Person getWearer() {
            return wearer;
        }

        public void setWearer(Person wearer) {
            this.wearer = wearer;
        }

        @Override
        public void grab(Person someone) {
            this.wearer = someone;
            System.out.print(someone.getName() + " взял колпак. ");
        }
    }

}
