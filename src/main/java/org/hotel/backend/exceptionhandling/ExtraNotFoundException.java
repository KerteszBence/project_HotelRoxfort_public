package org.hotel.backend.exceptionhandling;



public class ExtraNotFoundException extends RuntimeException {
    private final Long id;

    public ExtraNotFoundException(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}