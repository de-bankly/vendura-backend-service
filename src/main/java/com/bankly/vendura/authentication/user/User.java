package com.bankly.vendura.authentication.user;

import com.bankly.vendura.authentication.roles.IRole;
import com.bankly.vendura.authentication.roles.Role;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "users")
@NoArgsConstructor
@AllArgsConstructor
public class User implements IUser {

  @Id private String id;

  @Indexed(unique = true)
  private String username;

  private String password;

  @DBRef private Set<Role> roles = new HashSet<>();

  public Set<? extends IRole> getRoles() {
    return roles;
  }

}
