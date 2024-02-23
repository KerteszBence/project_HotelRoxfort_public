package org.hotel.backend.exceptionhandling;



public class BookingByRoomNotFoundException extends RuntimeException {

    private final Long roomId;

    public BookingByRoomNotFoundException(Long id) {
        this.roomId = id;
    }

    public Long getRoomId() {
        return roomId;
    }
}