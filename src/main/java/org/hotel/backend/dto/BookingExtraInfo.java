package org.hotel.backend.dto;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingExtraInfo {
    private Integer quantity;
    private Long extraId;
    private Long bookingId;
}