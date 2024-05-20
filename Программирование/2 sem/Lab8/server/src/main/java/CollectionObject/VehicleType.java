package CollectionObject;

public enum VehicleType {
    BOAT("BOAT"),
    HOVERBOARD("HOVERBOARD"),
    SPACESHIP("SPACESHIP");

    private final String type;
    VehicleType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
