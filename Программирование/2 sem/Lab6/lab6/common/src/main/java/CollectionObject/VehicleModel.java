package CollectionObject;

import java.io.Serializable;
import java.util.Date;

public class VehicleModel implements Serializable {
    private String name;
    private Coordinates coordinates;
    private Double enginePower;
    private Float capacity;
    private float distanceTravelled;
    private VehicleType type;

    public VehicleModel(String name, Coordinates coordinates, Double enginePower, Float capacity, float distanceTravelled, VehicleType type) {
        this.name = name;
        this.coordinates = coordinates;
        this.enginePower = enginePower;
        this.capacity = capacity;
        this.distanceTravelled = distanceTravelled;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public Double getEnginePower() {
        return enginePower;
    }

    public Float getCapacity() {
        return capacity;
    }

    public float getDistanceTravelled() {
        return distanceTravelled;
    }

    public VehicleType getType() {
        return type;
    }

    @Override
    public String toString() {
        return "VehicleModel{" +
                "name='" + name + '\'' +
                ", coordinates=" + coordinates +
                ", enginePower=" + enginePower +
                ", capacity=" + capacity +
                ", distanceTravelled=" + distanceTravelled +
                ", type=" + type +
                '}';
    }
}
