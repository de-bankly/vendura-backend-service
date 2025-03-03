package com.bankly.vendura.authentication.roles;

import com.bankly.vendura.authentication.roles.model.IRole;
import com.bankly.vendura.authentication.roles.model.Role;
import com.bankly.vendura.authentication.roles.model.RoleRepository;
import java.util.Optional;

import com.bankly.vendura.exceptions.EntityCreationException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class RoleService {

  private final RoleRepository roleRepository;

  public Optional<IRole> findRoleByName(String name) {
    if (name.startsWith("ROLE_")) {
      name = name.substring(5);
    }
    return Optional.ofNullable(this.roleRepository.findRoleByName(name).orElse(null));
  }

  public IRole createRole(String roleName) throws EntityCreationException {
    if (this.findRoleByName(roleName).isPresent()) {
      throw new EntityCreationException("Role " + roleName + " already exists");
    }
    Role role = new Role();
    role.setName(roleName);
    return this.roleRepository.save(role);
  }
}
