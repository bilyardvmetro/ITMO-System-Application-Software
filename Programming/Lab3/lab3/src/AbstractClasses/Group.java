package AbstractClasses;

public abstract class Group {
    private String group_name;
    {
        group_name = "группа людей";
    }

    public Group(String group_name){
        this.setGroup_name(group_name);
    }

    public void setGroup_name(String group_name){
        this.group_name = group_name;
    }

    public String getGroup_name() {
        return group_name;
    }

    @Override
    public int hashCode() {
        return super.hashCode() + this.getGroup_name().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj.hashCode() == this.hashCode();
    }

    @Override
    public String toString() {
        return "Имя группы: " + getGroup_name();
    }
}
