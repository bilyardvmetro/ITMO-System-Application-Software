package AbstractClasses;

public abstract class BodyPart {
    private String name;

    public Item equipment;
    {
        name = "часть тела";
    }

    public BodyPart(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Item getEquipment() {
        return equipment;
    }
}
