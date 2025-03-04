package com.bankly.vendura.authentication.user;

import java.util.Optional;
import java.util.Set;

import com.bankly.vendura.authentication.roles.model.IRole;
import com.bankly.vendura.authentication.roles.model.Role;
import com.bankly.vendura.authentication.roles.RoleService;
import com.bankly.vendura.authentication.user.model.IUser;
import com.bankly.vendura.authentication.user.model.User;
import com.bankly.vendura.authentication.user.model.UserRepository;
import com.bankly.vendura.utilities.exceptions.EntityCreationException;
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
  public IUser createUser(String username, String passwordPlain, Set<String> roles) throws EntityCreationException {

    if (this.userRepository.findUserByUsername(username).isPresent()) {
      throw new EntityCreationException("Username " + username + " already exists", HttpStatus.CONFLICT, "User", true);
    }

    User user = new User();
    user.setUsername(username);
    user.setPassword(this.passwordEncoder.encode(passwordPlain));

    for (String role : roles) {
      Optional<IRole> optionalRole = this.roleService.findRoleByName(role);
        user.getRoles().add((Role) optionalRole.orElseThrow(() -> new EntityCreationException("Role " + role + " not found", HttpStatus.UNPROCESSABLE_ENTITY, "User", true)));
    }

    return this.userRepository.save(user);
  }

  public IUser getUser(String username) {
    Optional<User> optionalUser = this.userRepository.findUserByUsername(username);
    return optionalUser.orElse(null);
  }

}
