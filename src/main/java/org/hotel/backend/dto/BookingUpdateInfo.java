package org.hotel.backend.dto;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingUpdateInfo {
    private Long updateRoomId;
    private LocalDateTime inDate;
    private LocalDateTime outDate;
}