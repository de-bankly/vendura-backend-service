package com.bankly.vendura.authentication.user.model;

import com.bankly.vendura.authentication.roles.model.Role;
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
public class User {

  @Id private String id;

  @Indexed(unique = true)
  private String username;

  private String password;

  @DBRef private Set<Role> roles = new HashSet<>();

  private boolean enabled;
  private boolean locked;

}
