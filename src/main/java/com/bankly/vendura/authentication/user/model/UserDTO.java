package com.bankly.vendura.authentication.user.model;

import java.util.Set;
import java.util.stream.Collectors;

import com.bankly.vendura.authentication.roles.model.RoleDTO;
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

  public User toUser() {
    return new User(this.id, this.username, this.password, this.roles.stream().map(RoleDTO::toRole).collect(Collectors.toSet()));
  }

  public static UserDTO fromUser(User user) {
    return new UserDTO(user.getId(), user.getUsername(), user.getPassword(), user.getRoles().stream().map(RoleDTO::fromRole).collect(Collectors.toSet()));
  }

}
