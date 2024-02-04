package Classes.Entities;

public class SpaceConference {
    protected String name;
    public SpaceConference(){
        setName("Космическая конференция");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String endUp() {
        return this.getName() + " закончилась довольно поздно";
    }
}
