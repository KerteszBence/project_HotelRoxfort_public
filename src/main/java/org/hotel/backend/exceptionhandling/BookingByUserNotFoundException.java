package org.hotel.backend.exceptionhandling;



public class BookingByUserNotFoundException extends RuntimeException {
    private final Long userId;

    public BookingByUserNotFoundException(Long id) {
        this.userId = id;
    }

    public Long getUserId() {
        return userId;
    }
}