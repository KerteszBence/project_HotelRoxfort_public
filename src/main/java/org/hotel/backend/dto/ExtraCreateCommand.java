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
public class ExtraCreateCommand {

    @NotBlank(message = "Extra description cannot be blank.")
    @NotNull(message = "Extra description cannot be null.")
    @NotEmpty(message = "Extra description cannot be empty.")
    private String extraDescription;

    @Positive(message = "Price must be a positive number.")
    private double price;
}