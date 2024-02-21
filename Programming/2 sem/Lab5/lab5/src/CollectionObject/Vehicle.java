package CollectionObject;

import java.util.Date;
import java.util.Objects;

public class Vehicle {
    private Long id; //Поле не может быть null, Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private String name; //Поле не может быть null, Строка не может быть пустой
    private Coordinates coordinates; //Поле не может быть null
    private java.util.Date creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически
    private Double enginePower; //Поле не может быть null, Значение поля должно быть больше 0
    private Float capacity; //Поле не может быть null, Значение поля должно быть больше 0
    private float distanceTravelled; //Значение поля должно быть больше 0
    private VehicleType type; //Поле не может быть null

    public Vehicle(Long id, String name, Coordinates coordinates, Date creationDate, Double enginePower, Float capacity, float distanceTravelled, VehicleType type) {
        this.id = id;
        this.name = name;
        this.coordinates = coordinates;
        this.creationDate = creationDate;
        this.enginePower = enginePower;
        this.capacity = capacity;
        this.distanceTravelled = distanceTravelled;
        this.type = type;
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

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Vehicle vehicle = (Vehicle) object;
        return Float.compare(distanceTravelled, vehicle.distanceTravelled) == 0 && Objects.equals(id, vehicle.id) && Objects.equals(name, vehicle.name) && Objects.equals(coordinates, vehicle.coordinates) && Objects.equals(creationDate, vehicle.creationDate) && Objects.equals(enginePower, vehicle.enginePower) && Objects.equals(capacity, vehicle.capacity) && type == vehicle.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, coordinates, creationDate, enginePower, capacity, distanceTravelled, type);
    }

    @Override
    public String toString() {
        return "Транспортное средство{" + "\n" +
                "id: " + id + "\n" +
                "Имя: " + name + "\n" +
                coordinates + "\n" +
                "Дата создания: " + creationDate + "\n" +
                "Мощность двигателя: " + enginePower + "\n" +
                "Объем двигателя: " + capacity + "\n" +
                "Пробег: " + distanceTravelled + "\n" +
                "Тип: " + type + "\n" +
                '}';
    }
}
