package com.bankly.vendura.authentication.user;

import com.bankly.vendura.authentication.roles.RoleService;
import com.bankly.vendura.authentication.roles.model.Role;
import com.bankly.vendura.authentication.user.model.User;
import com.bankly.vendura.authentication.user.model.UserRepository;
import com.bankly.vendura.utilities.exceptions.EntityCreationException;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserService {

  private final RoleService roleService;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Transactional
  public User createUser(String username, String passwordPlain, Set<String> roles)
      throws EntityCreationException {

    if (this.userRepository.findUserByUsername(username).isPresent()) {
      throw new EntityCreationException(
          "Username " + username + " already exists", HttpStatus.CONFLICT, "User", true);
    }

    User user = new User();
    user.setUsername(username);
    user.setPassword(this.passwordEncoder.encode(passwordPlain));

    for (String role : roles) {
      Optional<Role> optionalRole = this.roleService.findRoleByName(role);

      if (optionalRole.isEmpty()) {
        throw new EntityCreationException(
            "Role " + role + " not found", HttpStatus.UNPROCESSABLE_ENTITY, "User", true);
      }

      Role roleObj = optionalRole.get();

      if (!roleObj.isActive()) {
        throw new EntityCreationException(
            "Role " + role + " cannot be assigned due to being inactive",
            HttpStatus.CONFLICT,
            "User",
            true);
      }

      user.getRoles().add(roleObj);
    }

    return this.userRepository.save(user);
  }

  public User getUser(String username) {
    Optional<User> optionalUser = this.userRepository.findUserByUsername(username);
    return optionalUser.orElse(null);
  }
}
