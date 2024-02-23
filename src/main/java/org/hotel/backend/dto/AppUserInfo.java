package org.hotel.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hotel.backend.domain.UserRole;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppUserInfo {
    private String firstName;
    private String lastName;
    private String email;
    private List<UserRole> roles;
}