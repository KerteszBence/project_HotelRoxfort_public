package org.hotel.backend.dto;



import org.hotel.backend.domain.UserRole;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.stream.Collectors;

public class AuthenticatedUserInfo {

    private String username;
    private List<UserRole> roles;

    public AuthenticatedUserInfo(UserDetails userDetails) {
        this.username = userDetails.getUsername();
        this.roles = parseRoles(userDetails);
    }

    private List<UserRole> parseRoles(UserDetails user) {
        return user.getAuthorities()
                .stream()
                .map(authority -> UserRole.valueOf(authority.getAuthority()))
                .collect(Collectors.toList());
    }

    public String getUsername() {
        return username;
    }

    public List<UserRole> getRoles() {
        return roles;
    }
}