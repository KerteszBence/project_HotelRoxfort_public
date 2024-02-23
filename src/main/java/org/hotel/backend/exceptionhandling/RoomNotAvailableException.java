package org.hotel.backend.exceptionhandling;



import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

public class RoomNotAvailableException extends RuntimeException {
    private final long id;
    private final LocalDateTime inDate;
    private final LocalDateTime outDate;

    public RoomNotAvailableException(@Positive(message = "Room id must be a positive number.") Long roomId, LocalDateTime inDate, LocalDateTime outDate) {
        this.id = roomId;
        this.inDate = inDate;
        this.outDate = outDate;
    }

    public long getId() {
        return id;
    }

    public LocalDateTime getInDate() {
        return inDate;
    }

    public LocalDateTime getOutDate() {
        return outDate;
    }
}