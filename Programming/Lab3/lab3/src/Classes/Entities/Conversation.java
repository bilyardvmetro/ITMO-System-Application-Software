package Classes.Entities;

import AbstractClasses.Person;

public class Conversation {
    protected String name;

    public Conversation(){
        setName("передача беседы");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String organize(Person person){
        return " была организована " + getName() + " " + person.getName();
    }
}
