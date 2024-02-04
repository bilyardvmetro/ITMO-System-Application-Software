package Classes.Groups;

import AbstractClasses.Group;
import AbstractClasses.Person;

public class GroupOfScientists extends Group {
    protected Person Scientist1;
    protected Person Scientist2;
    public GroupOfScientists(Person scientist1, Person scientist2){
        super("Альфа и Мемега");
        setScientist1(scientist1);
        setScientist2(scientist2);
    }

    public void setScientist1(Person scientist1) {
        Scientist1 = scientist1;
    }

    public void setScientist2(Person scientist2) {
        Scientist2 = scientist2;
    }

    public String getScientist1Name() {
        return Scientist1.getName();
    }

    public String getScientist2Name() {
        return Scientist2.getName();
    }

    public String askSomebody(Person smb){
        return getScientist1Name() + " и " + getScientist2Name() + " обстоятельно расспросили " + smb.getName();
    }
}
