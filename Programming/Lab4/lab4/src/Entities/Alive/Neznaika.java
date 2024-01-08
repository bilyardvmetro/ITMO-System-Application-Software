package Entities.Alive;

import AbstractClasses.BodyPart;
import AbstractClasses.Clothing;
import AbstractClasses.Item;
import AbstractClasses.Person;
import Enums.Mood;
import Interfaces.Grabable;
import Interfaces.PutOnItem;

public class Neznaika extends Person implements PutOnItem {
    public LeftArm leftArm;
    public RightArm rightArm;
    public Head head;
    public Body body;
    public Nose nose;

    public Neznaika(String name) {
        super(name);
        leftArm = new LeftArm("левая рука");
        rightArm = new RightArm("правая рука");
        head = new Head("голова");
        body = new Body("туловище");
        nose = new Nose("нос");
    }

    public class LeftArm extends BodyPart{
        private Person owner = Neznaika.this;
        public LeftArm(String name) {
            super(name);
        }

        public Person getOwner() {
            return owner;
        }
    }
    public class RightArm extends BodyPart{
        private Person owner = Neznaika.this;
        public RightArm(String name) {
            super(name);
        }

        public Person getOwner() {
            return owner;
        }
    }
    public class Head extends BodyPart{
        private Person owner = Neznaika.this;
        public Head(String name) {
            super(name);
        }

        public Person getOwner() {
            return owner;
        }
    }
    public class Body extends BodyPart{
        private Person owner = Neznaika.this;

        public Body(String name) {
            super(name);
        }

        public Person getOwner() {
            return owner;
        }
    }
    public class Nose extends BodyPart{
        private Person owner = Neznaika.this;

        public Nose(String name) {
            super(name);
        }

        public Person getOwner() {
            return owner;
        }
    }
    @Override
    public void put_on_item(Clothing item, BodyPart body_part){
        item.grab(this);
        body_part.equipment = item;
        System.out.print("Незнайка надел " + item.getName() + " на " + body_part.getName() + ". ");
    }

    public void stop() {
        System.out.print("Незнайка остановился. ");
    }

    public void fear(){
        this.setMood(Mood.FEAR);
        System.out.print("Незнайка испугался. ");
        this.giggle();
        System.out.print(", чтобы показать, что ему не страшно. ");
    }

    public void giggle(){
        System.out.print("Незнайка начал хихикать ");
    }

    public void grab_item(Grabable item) {
        item.grab(this);
        if (leftArm.equipment == null) {
            leftArm.equipment = (Item) item;
        } else if (rightArm.equipment == null) {
            rightArm.equipment = (Item) item;
        }
    }
    public void grapple(Grabable thing){
        thing.grab(this);
    }
}
