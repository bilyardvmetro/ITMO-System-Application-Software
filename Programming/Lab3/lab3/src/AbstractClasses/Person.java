package AbstractClasses;

public abstract class Person {
    private String name;
    {
        name = "человек";
    }
    public Person(String name){
        this.setName(name);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static String askProperly(Person person1, Person person2, Person person3){
        return person1.getName() + " и " + person2.getName() + " обстоятельно расспросили " + person3.getName();
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
        return "Имя человека: " + getName();
    }
}
