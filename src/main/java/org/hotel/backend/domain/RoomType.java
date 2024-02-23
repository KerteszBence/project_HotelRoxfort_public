package org.hotel.backend.domain;



public enum RoomType {
    SINGLE("SINGLE"),
    DOUBLE("DOUBLE"),
    TWIN("TWIN"),
    TRIPLE("TRIPLE"),
    SUITE("SUITE");

    private final String displayName;

    RoomType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static int determineCapacityByType(RoomType roomType) {
        switch (roomType) {
            case SINGLE:
                return 1;
            case DOUBLE:
            case TWIN:
                return 2;
            case TRIPLE:
            case SUITE:
                return 3;
            default:
                throw new IllegalArgumentException("Invalid room type: " + roomType);
        }
    }
}