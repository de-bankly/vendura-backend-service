package com.bankly.vendura.authentication.user.model;

import com.bankly.vendura.authentication.roles.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private String id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private Boolean enabled;
    private Boolean locked;
    private Set<String> roles;
    
    public static UserDTO fromUser(User user) {
        if (user == null) {
            return null;
        }
        
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .enabled(user.isEnabled())
                .locked(user.isLocked())
                .roles(user.getRoles().stream()
                        .map(Role::getId)
                        .collect(Collectors.toSet()))
                .build();
    }
}
