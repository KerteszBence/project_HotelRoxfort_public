package org.hotel.backend.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hotel.backend.domain.FileRegistry;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HouseCreateCommand {

    @NotBlank(message = "House name cannot be blank.")
    @NotNull(message = "House name cannot be null.")
    @NotEmpty(message = "House name cannot be empty.")
    private String houseName;

    @NotBlank(message = "House route cannot be blank.")
    @NotNull(message = "House route cannot be null.")
    @NotEmpty(message = "House route cannot be empty.")
    private String houseRoute;

    @NotBlank(message = "House description cannot be blank.")
    @NotNull(message = "House description cannot be null.")
    @NotEmpty(message = "House description cannot be empty.")
    private String houseDescription;

    private List<FileRegistry> fileRegistryList;
}