package com.bankly.vendura.authentication.roles;

import com.bankly.vendura.authentication.roles.model.Role;
import com.bankly.vendura.authentication.roles.model.RoleRepository;
import com.bankly.vendura.utilities.exceptions.EntityCreationException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class RoleService {

  private final RoleRepository roleRepository;

  public Optional<Role> findRoleByName(String name) {
    if (name.startsWith("ROLE_")) {
      name = name.substring(5);
    }
    return this.roleRepository.findRoleByName(name);
  }

  public Optional<Role> findRoleById(String id) {
    return this.roleRepository.findById(id);
  }

  @Transactional
  public Role createRole(String roleName) throws EntityCreationException {
    if (this.findRoleByName(roleName).isPresent()) {
      throw new EntityCreationException(
          "Role " + roleName + " already exists", HttpStatus.CONFLICT, "Role", true);
    }
    Role role = new Role();
    role.setName(roleName);
    role.setActive(true);
    return this.roleRepository.save(role);
  }
}
