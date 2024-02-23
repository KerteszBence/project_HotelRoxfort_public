package org.hotel.backend.dto;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingUpdateCommand {

    @Positive(message = "Room id must be a positive number.")
    private Long updateRoomId;

    @FutureOrPresent
    private LocalDateTime inDate;

    @Future
    private LocalDateTime outDate;

    @AssertTrue(message = "Ending date must be after starting date")
    private boolean isEndingAfterStart() {
        return outDate == null || outDate.isAfter(inDate);
    }
}