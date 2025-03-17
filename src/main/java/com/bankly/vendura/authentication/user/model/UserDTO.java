package com.bankly.vendura.authentication.user.model;

import com.bankly.vendura.authentication.roles.model.RoleDTO;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
  private String id;
  private String username;
  private String password;
  private Set<RoleDTO> roles;
  private Boolean enabled;
  private Boolean locked;

  public static UserDTO fromUser(User user) {
    return new UserDTO(
        user.getId(),
        user.getUsername(),
        user.getPassword(),
        user.getRoles().stream().map(RoleDTO::fromRole).collect(Collectors.toSet()),
        user.isEnabled(),
        user.isLocked());
  }
}
