package com.bankly.vendura.authentication.user.model;

import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
}
