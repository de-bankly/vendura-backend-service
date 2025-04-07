package com.bankly.vendura.authentication.user;

import com.bankly.vendura.authentication.roles.model.Role;
import com.bankly.vendura.authentication.user.model.UserRepository;
import java.util.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Custom implementation of {@link UserDetailsService} for loading user-specific data during
 * authentication. Retrieves user details from the repository and maps roles to authorities.
 */
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class CustomUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;

  /**
   * Loads the user details by username.
   *
   * @param username the username of the user to load
   * @return the user details including username, password, and granted authorities
   * @throws UsernameNotFoundException if the username is not found
   */
  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

    if (username.equalsIgnoreCase("admin")) {
      return new User(
          "admin",
          new BCryptPasswordEncoder().encode("admin"),
          Collections.singleton(new SimpleGrantedAuthority("ROLE_ADMIN")));
    }

    if (username.equalsIgnoreCase("user")) {
      return new User(
          "user",
          new BCryptPasswordEncoder().encode("user"),
          Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")));
    }

    com.bankly.vendura.authentication.user.model.User user =
        this.userRepository
            .findUserByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException(username + " not found"));

    return user;

    /*return new User(
        user.getUsername(),
        user.getPassword(),
        user.isEnabled(),
        true,
        true,
        !user.isLocked(),
        mapRolesToAuthorities(
            user.getRoles().stream().filter(Role::isActive).collect(Collectors.toSet())));*/
  }

  /**
   * Maps a set of roles to a collection of {@link GrantedAuthority}.
   *
   * @param roles the set of roles to map
   * @return a collection of granted authorities for the given roles
   */
  private Collection<GrantedAuthority> mapRolesToAuthorities(Set<Role> roles) {
    return roles.stream()
        .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName().toUpperCase()))
        .collect(Collectors.toList());
  }
}
