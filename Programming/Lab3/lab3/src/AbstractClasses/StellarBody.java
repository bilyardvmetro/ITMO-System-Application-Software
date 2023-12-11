package AbstractClasses;

import Enums.StellarBodyType;

public abstract class StellarBody {
    private String name;
    private StellarBodyType type;
    {
        name = "звёздное тело";
    }
    public StellarBody(String name, StellarBodyType type){
        this.setName(name);
        this.setType(type);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setType(StellarBodyType type) {
        this.type = type;
    }
    public StellarBodyType getType() {
        return type;
    }

    @Override
    public int hashCode() {
        return super.hashCode() + this.getName().hashCode();
    }

    @Override
    public boolean equals(Object obj){
        return obj.hashCode() == this.hashCode();
    }

    @Override
    public String toString(){
        return "Название звёздного тела: " + getName();
    }
}
