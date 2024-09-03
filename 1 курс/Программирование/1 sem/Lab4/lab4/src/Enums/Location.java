package Enums;

public enum Location {
    START_LOCATION("стартовая локация"),
    RIVER_SHORE ("берег реки"),
    ANOTHER_RIVER_SHORE("другой берег реки"),
    BRIDGE("мост"),
    HOUSE("порог белого дома с зелёной крышей"),
    STREET("улица"),
    HALLWAY("коридор"),
    NURSE_OFFICE("кабинет врача"),
    HOSPITAL_ROOM("больничная палата"),
    UNNOWN_LOCATION("неизвестное направление"),
    VORCHUNS_BUNK("койка Ворчуна");
    private final String title;
    Location(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
