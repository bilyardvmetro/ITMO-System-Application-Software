package CollectionObject;

public enum VehicleType {
    BOAT("Лодка"),
    HOVERBOARD("Ховерборд"),
    SPACESHIP("Космический корабль");

    private final String type;
    VehicleType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
