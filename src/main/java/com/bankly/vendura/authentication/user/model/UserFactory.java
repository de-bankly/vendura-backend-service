package com.bankly.vendura.authentication.user.model;

import com.bankly.vendura.authentication.roles.model.Role;
import java.util.stream.Collectors;

public class UserFactory {

  public static UserDTO toDTO(User user) {
    if (user == null) {
      return null;
    }

    return UserDTO.builder()
        .id(user.getId())
        .username(user.getUsername())
        .firstName(user.getFirstName())
        .lastName(user.getLastName())
        .email(user.getEmail())
        .enabled(user.isEnabled())
        .locked(user.isLocked())
        .roles(user.getRoles().stream().map(Role::getId).collect(Collectors.toSet()))
        .build();
  }

  public static User toEntity(UserDTO userDTO) {
    if (userDTO == null) {
      return null;
    }

    User user = new User();
    user.setId(userDTO.getId());
    user.setUsername(userDTO.getUsername());
    user.setFirstName(userDTO.getFirstName());
    user.setLastName(userDTO.getLastName());
    user.setEmail(userDTO.getEmail());
    user.setPassword(userDTO.getPassword());
    user.setEnabled(userDTO.getEnabled() == null || userDTO.getEnabled());
    user.setLocked(userDTO.getLocked() == null || userDTO.getLocked());

    // Note: Converting Role IDs to Role entities would require a RoleRepository,
    // which is not available to this factory method. This should be handled at the service level.

    return user;
  }
}
