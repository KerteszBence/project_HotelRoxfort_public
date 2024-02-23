package org.hotel.backend.exceptionhandling;



public class BookingNotFoundException extends RuntimeException {
    private final Long bookingId;

    public BookingNotFoundException(Long id) {
        this.bookingId = id;
    }

    public Long getBookingId() {
        return bookingId;
    }
}