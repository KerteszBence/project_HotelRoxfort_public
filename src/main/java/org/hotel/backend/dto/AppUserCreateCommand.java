package org.hotel.backend.dto;




import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppUserCreateCommand {

    @NotBlank(message = "firstName cannot be blank.")
    @NotNull(message = "firstName cannot be null.")
    @NotEmpty(message = "firstName cannot be empty.")
    private String firstName;

    @NotBlank(message = "lastName cannot be blank.")
    @NotNull(message = "lastName cannot be null.")
    @NotEmpty(message = "lastName cannot be empty.")
    private String lastName;

    @NotBlank(message = "email cannot be blank.")
    @NotNull(message = "email cannot be null.")
    @NotEmpty(message = "email cannot be empty.")
    @Email(message = "Invalid email address.")
    private String email;

    @NotBlank(message = "password cannot be blank.")
    @NotNull(message = "password cannot be null.")
    @NotEmpty(message = "password cannot be empty.")
    private String password;
}