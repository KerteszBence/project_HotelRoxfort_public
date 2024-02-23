package org.hotel.backend.exceptionhandling;



public class UserNotFoundException extends RuntimeException {
    private final Long appUserId;

    public UserNotFoundException(Long appUserId) {
        this.appUserId = appUserId;
    }

    public Long getId() {
        return appUserId;
    }
}