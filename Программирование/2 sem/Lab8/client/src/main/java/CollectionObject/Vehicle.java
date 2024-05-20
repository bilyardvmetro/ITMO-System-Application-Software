package CollectionObject;

import java.io.Serializable;
import java.util.Date;

public class Vehicle implements Serializable {
    private Long id; //Поле не может быть null, Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private String name; //Поле не может быть null, Строка не может быть пустой
    private Coordinates coordinates; //Поле не может быть null
    private Date creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически
    private Double enginePower; //Поле не может быть null, Значение поля должно быть больше 0
    private Float capacity; //Поле не может быть null, Значение поля должно быть больше 0
    private float distanceTravelled; //Значение поля должно быть больше 0
    private VehicleType type; //Поле не может быть null
    private String creator;

    public Vehicle(Long id, String name, Coordinates coordinates, Date creationDate, Double enginePower, Float capacity,
                   float distanceTravelled, VehicleType type, String creator) {
        this.id = id;
        this.name = name;
        this.coordinates = coordinates;
        this.creationDate = creationDate;
        this.enginePower = enginePower;
        this.capacity = capacity;
        this.distanceTravelled = distanceTravelled;
        this.type = type;
        this.creator = creator;
    }

    public Vehicle() {
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Double getEnginePower() {
        return enginePower;
    }

    public void setEnginePower(Double enginePower) {
        this.enginePower = enginePower;
    }

    public Float getCapacity() {
        return capacity;
    }

    public void setCapacity(Float capacity) {
        this.capacity = capacity;
    }

    public float getDistanceTravelled() {
        return distanceTravelled;
    }

    public void setDistanceTravelled(float distanceTravelled) {
        this.distanceTravelled = distanceTravelled;
    }

    public VehicleType getType() {
        return type;
    }

    public void setType(VehicleType type) {
        this.type = type;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    @Override
    public String toString() {
        return "\n\nVehicle{\n" +
                "id = " + id +
                ", \nname = '" + name + '\'' +
                ", \ncoordinates = " + coordinates +
                ", \ncreationDate = " + creationDate +
                ", \nenginePower = " + enginePower +
                ", \ncapacity = " + capacity +
                ", \ndistanceTravelled = " + distanceTravelled +
                ", \ntype = " + type +
                ", \ncreator = '" + creator + '\'' +
                '}';
    }
}
