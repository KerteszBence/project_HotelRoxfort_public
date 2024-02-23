package org.hotel.backend.domain;



public enum RoomStatus {
    AVAILABLE("AVAILABLE"),
    RESERVED("RESERVED"),
    BOOKED("BOOKED"),
    NOT_AVAILABLE("NOT_AVAILABLE");

    private final String displayName;

    RoomStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}