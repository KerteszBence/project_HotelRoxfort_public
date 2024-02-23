package org.hotel.backend.exceptionhandling;



public class RoomNotFoundException extends RuntimeException {
    private final Long roomId;

    public RoomNotFoundException(Long roomId) {
        this.roomId = roomId;
    }

    public Long getRoomId() {
        return roomId;
    }
}