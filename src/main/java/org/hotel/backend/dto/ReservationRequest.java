package org.hotel.backend.dto;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationRequest {

    private Long houseId;
    private LocalDateTime inDate;
    private LocalDateTime outDate;
    private int numberOfGuests;
}