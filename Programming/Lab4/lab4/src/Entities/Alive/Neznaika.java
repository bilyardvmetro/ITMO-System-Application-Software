package Entities.Alive;

import AbstractClasses.BodyPart;
import AbstractClasses.Clothing;
import AbstractClasses.Item;
import AbstractClasses.Person;
import Entities.Inanimate.Glasses;
import Entities.Inanimate.Hanger;
import Enums.Mood;
import Exceptions.BodyTypeMismatchException;
import Exceptions.GrabableMismatchException;
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
    public void put_on_item(Clothing item, BodyPart body_part) throws BodyTypeMismatchException {
        if ((item instanceof Hanger.LabCoat && body_part != body)
                | (item instanceof Hanger.Cap && body_part != head)
                | (item instanceof Glasses && body_part != nose)){
            throw new BodyTypeMismatchException("ВЫ НЕ МОЖЕТЕ НАДЕТЬ ЭТОТ ПРЕДМЕТ НА ЭТУ ЧАСТЬ ТЕЛА"); // checked exception
        }
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
            System.out.print(" в левую руку. ");
        } else if (rightArm.equipment == null) {
            rightArm.equipment = (Item) item;
            System.out.print(" в правую руку. ");
        }
    }
    public void grapple(Grabable thing){
        if (thing instanceof Clothing ){
            throw new GrabableMismatchException("Вы не можете ухватиться за этот предмет"); // unchecked exception
        }
        thing.grab(this);
    }
}
