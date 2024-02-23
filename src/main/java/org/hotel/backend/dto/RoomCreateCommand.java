package org.hotel.backend.dto;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomCreateCommand {

    @Positive(message = "Room number must be a positive number.")
    private Long roomNumber;

    @NotBlank(message = "Room description cannot be blank.")
    @NotNull(message = "Room description cannot be null.")
    @NotEmpty(message = "Room description cannot be empty.")
    private String roomDescription;

    @NotBlank(message = "Room type cannot be blank")
    private String roomType;

    private double pricePerNight;

//    private String status;

    @Positive(message = "Room house must be a positive number.")
    private long houseId;
}