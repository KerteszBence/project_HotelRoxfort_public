package org.hotel.backend.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hotel.backend.domain.UserRole;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppUserListInfo {
    private String firstName;
    private String lastName;
    private String email;
    private List<UserRole> roles;
}