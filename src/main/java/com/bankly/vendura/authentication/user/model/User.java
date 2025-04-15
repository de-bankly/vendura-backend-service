package com.bankly.vendura.authentication.user.model;

import com.bankly.vendura.authentication.roles.model.Role;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Data
@Document(collection = "users")
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {

  @Id private String id;

  @Indexed(unique = true)
  private String username;

  private String password;

  @DBRef private Set<Role> roles = new HashSet<>();

  private boolean enabled;
  private boolean locked;

  public User(String id) {
    this.id = id;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return this.roles.stream()
        .filter(Role::isActive)
        .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
        .collect(Collectors.toList());
  }

  @Override
  public boolean isAccountNonLocked() {
    return !this.locked;
  }

}
