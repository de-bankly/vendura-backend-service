package com.bankly.vendura.authentication.user;

import com.bankly.vendura.authentication.roles.model.Role;
import com.bankly.vendura.authentication.roles.model.RoleDTO;
import com.bankly.vendura.authentication.roles.model.RoleRepository;
import com.bankly.vendura.authentication.user.model.User;
import com.bankly.vendura.authentication.user.model.UserDTO;
import com.bankly.vendura.authentication.user.model.UserRepository;
import com.bankly.vendura.utilities.exceptions.EntityCreationException;
import com.bankly.vendura.utilities.exceptions.EntityRetrieveException;
import com.bankly.vendura.utilities.exceptions.EntityUpdateException;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

  public User createUser(UserDTO userDTO) throws EntityCreationException {

    String username = userDTO.getUsername();
    String passwordPlain = userDTO.getPassword();

    this.userRepository
        .findUserByUsername(username)
        .orElseThrow(
            () ->
                new EntityCreationException(
                    "Username " + username + " already exists", HttpStatus.CONFLICT, "User", true));

    User user = new User();
    user.setUsername(username);
    user.setPassword(this.passwordEncoder.encode(passwordPlain));

    user.setEnabled(userDTO.getEnabled() == null || userDTO.getEnabled());
    user.setLocked(userDTO.getLocked() == null || userDTO.getLocked());

    Set<String> roleIds =
        userDTO.getRoles().stream().map(RoleDTO::getId).collect(Collectors.toSet());

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

    if (userDTO.getLocked() != null && user.isLocked() != userDTO.getLocked()) {
      user.setLocked(userDTO.getLocked());
    }

    if (userDTO.getEnabled() != null && user.isEnabled() != userDTO.getEnabled()) {
      user.setEnabled(userDTO.getEnabled());
    }

    return this.userRepository.save(user);
  }
}
