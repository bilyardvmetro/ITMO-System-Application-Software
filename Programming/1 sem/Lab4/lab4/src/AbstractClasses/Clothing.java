package AbstractClasses;

public abstract class Clothing extends Item{
    private String name;
    {
        name = "предмет одежды";
    }

    public Clothing(String name) {
        super(name);
    }

}
