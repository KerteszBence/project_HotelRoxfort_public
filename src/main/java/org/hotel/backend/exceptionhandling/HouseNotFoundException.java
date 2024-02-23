package org.hotel.backend.exceptionhandling;



public class HouseNotFoundException extends RuntimeException {
    private final Long id;

    public HouseNotFoundException(Long houseId) {
        this.id = houseId;
    }

    public Long getId() {
        return id;
    }
}