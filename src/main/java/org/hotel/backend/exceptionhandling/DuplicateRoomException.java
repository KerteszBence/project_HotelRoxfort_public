package org.hotel.backend.exceptionhandling;



public class DuplicateRoomException extends RuntimeException {

    private final Long roomNumber;
    private final Long houseId;

    public DuplicateRoomException(Long roomNumber, Long houseId) {
        this.roomNumber = roomNumber;
        this.houseId = houseId;
    }

    public Long getRoomNumber() {
        return roomNumber;
    }

    public Long getHouseId() {
        return houseId;
    }
}