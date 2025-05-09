package com.bankly.vendura.authentication.user;

import com.bankly.vendura.authentication.controller.models.Login;
import com.bankly.vendura.authentication.roles.model.Role;
import com.bankly.vendura.authentication.roles.model.RoleRepository;
import com.bankly.vendura.authentication.security.JWTService;
import com.bankly.vendura.authentication.user.model.User;
import com.bankly.vendura.authentication.user.model.UserDTO;
import com.bankly.vendura.authentication.user.model.UserRepository;
import com.bankly.vendura.utilities.exceptions.EntityCreationException;
import com.bankly.vendura.utilities.exceptions.EntityRetrieveException;
import com.bankly.vendura.utilities.exceptions.EntityUpdateException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final RoleRepository roleRepository;
  private final AuthenticationManager authenticationManager;
  private final JWTService jwtService;

  public User createUser(UserDTO userDTO) throws EntityCreationException {

    String username = userDTO.getUsername();
    String passwordPlain = userDTO.getPassword();

    this.userRepository
        .findUserByUsername(username)
        .ifPresent(
            _ -> {
              throw new EntityCreationException(
                  "Username " + username + " already exists", HttpStatus.CONFLICT, "User", true);
            });

    User user = new User();
    user.setUsername(username);
    user.setPassword(this.passwordEncoder.encode(passwordPlain));

    // Set additional user information if provided
    user.setFirstName(userDTO.getFirstName());
    user.setLastName(userDTO.getLastName());
    user.setEmail(userDTO.getEmail());

    user.setEnabled(userDTO.getEnabled() == null || userDTO.getEnabled());
    user.setLocked(userDTO.getLocked() == null || userDTO.getLocked());

    Set<String> roleIds = userDTO.getRoles();

    for (String roleId : roleIds) {
      Role role =
          this.roleRepository
              .findById(roleId)
              .orElseThrow(
                  () ->
                      new EntityRetrieveException(
                          "Role with " + roleId + " not found", HttpStatus.NOT_FOUND, roleId));

      if (!role.isActive()) {
        throw new EntityCreationException(
            "Role "
                + role.getName()
                + "("
                + role.getId()
                + ") cannot be assigned due to being inactive",
            HttpStatus.CONFLICT,
            "User",
            true);
      }

      user.getRoles().add(role);
    }

    return this.userRepository.save(user);
  }

  public User updateUser(String id, UserDTO userDTO) {
    if (userDTO.getId() != null) {
      throw new EntityUpdateException(
          "Cannot update user ID", HttpStatus.UNPROCESSABLE_ENTITY, "id");
    }

    User user =
        this.userRepository
            .findById(id)
            .orElseThrow(
                () ->
                    new EntityRetrieveException(
                        "User with ID " + id + " not found", HttpStatus.NOT_FOUND, id));

    if (userDTO.getUsername() != null && !user.getUsername().equals(userDTO.getUsername())) {
      this.userRepository
          .findUserByUsername(userDTO.getUsername())
          .orElseThrow(
              () ->
                  new EntityCreationException(
                      "User with username " + userDTO.getUsername() + " already exists",
                      HttpStatus.CONFLICT,
                      "User",
                      true));

      user.setUsername(userDTO.getUsername());
    }

    // Update user information if provided
    if (userDTO.getFirstName() != null) {
      user.setFirstName(userDTO.getFirstName());
    }

    if (userDTO.getLastName() != null) {
      user.setLastName(userDTO.getLastName());
    }

    if (userDTO.getEmail() != null) {
      user.setEmail(userDTO.getEmail());
    }

    if (userDTO.getPassword() != null) {
      user.setPassword(this.passwordEncoder.encode(userDTO.getPassword()));
    }

    if (userDTO.getLocked() != null && user.isLocked() != userDTO.getLocked()) {
      user.setLocked(userDTO.getLocked());
    }

    if (userDTO.getEnabled() != null && user.isEnabled() != userDTO.getEnabled()) {
      user.setEnabled(userDTO.getEnabled());
    }

    if (userDTO.getRoles() != null) {
      Set<Role> roles =
          userDTO.getRoles().stream()
              .map(
                  roleId ->
                      this.roleRepository
                          .findById(roleId)
                          .orElseThrow(
                              () ->
                                  new EntityUpdateException(
                                      "Cannot update roles because role with ID "
                                          + roleId
                                          + " not found",
                                      HttpStatus.NOT_FOUND,
                                      "roles")))
              .collect(Collectors.toSet());
      user.setRoles(roles);
    }

    return this.userRepository.save(user);
  }

  public Login.Response authenticate(Login.Request request) {
    Authentication authentication =
        this.authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

    SecurityContextHolder.getContext().setAuthentication(authentication);
    User user = (User) authentication.getPrincipal();

    String jwtToken = this.jwtService.generateToken(user);

    return new Login.Response(
        jwtToken,
        user.getUsername(),
        user.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toList()));
  }

  public Optional<User> findByUsername(String username) {
    return this.userRepository.findUserByUsername(username);
  }

  public Optional<User> findById(String id) {
    return this.userRepository.findById(id);
  }
}
