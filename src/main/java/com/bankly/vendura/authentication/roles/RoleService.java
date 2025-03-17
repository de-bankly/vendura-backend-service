package com.bankly.vendura.authentication.roles;

import com.bankly.vendura.authentication.roles.model.Role;
import com.bankly.vendura.authentication.roles.model.RoleDTO;
import com.bankly.vendura.authentication.roles.model.RoleRepository;
import com.bankly.vendura.utilities.exceptions.EntityCreationException;
import com.bankly.vendura.utilities.exceptions.EntityRetrieveException;
import com.bankly.vendura.utilities.exceptions.EntityUpdateException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class RoleService {

  private final RoleRepository roleRepository;

  public Optional<Role> findRoleByName(String name) {
    if (name.startsWith("ROLE_")) {
      name = name.substring(5);
    }
    return this.roleRepository.findRoleByName(name);
  }

  public Role createRole(RoleDTO roleDTO) {
    if (!roleDTO.getActive()) {
      throw new EntityCreationException(
          "Inactive role cannot be created", HttpStatus.UNPROCESSABLE_ENTITY, "Role", true);
    }

    if (this.findRoleByName(roleDTO.getName()).isPresent()) {
      throw new EntityCreationException(
          "Role " + roleDTO.getName() + " already exists", HttpStatus.CONFLICT, "Role", true);
    }

    Role role = new Role();

    if (roleDTO.getId() != null) {
      this.roleRepository
          .findById(roleDTO.getId())
          .orElseThrow(
              () ->
                  new EntityCreationException(
                      "Role with id "
                          + roleDTO.getId()
                          + " already exists, retry with another id or without to assign id automatically",
                      HttpStatus.CONFLICT,
                      "Role",
                      true));
    }

    role.setName(roleDTO.getName());
    role.setActive(true);

    return this.roleRepository.save(role);
  }

  public Role updateRole(String id, RoleDTO roleDTO) {
    Role role =
        this.roleRepository
            .findById(id)
            .orElseThrow(
                () ->
                    new EntityRetrieveException(
                        "Role cannot be updated, because id " + id + " was not found",
                        HttpStatus.NOT_FOUND,
                        id));

    if (roleDTO.getId() != null) {
      throw new EntityUpdateException(
          "Roles ID cannot be updated", HttpStatus.UNPROCESSABLE_ENTITY, "id");
    }

    if (roleDTO.getName() != null && !roleDTO.getName().equals(role.getName())) {

      this.roleRepository
          .findRoleByName(roleDTO.getName())
          .orElseThrow(
              () ->
                  new EntityUpdateException(
                      "Name " + roleDTO.getName() + " already exists",
                      HttpStatus.CONFLICT,
                      "name"));

      role.setName(roleDTO.getName());
    }

    if (roleDTO.getActive() != null && !roleDTO.getActive() == role.isActive()) {
      role.setActive(roleDTO.getActive());
    }

    return this.roleRepository.save(role);
  }

  public Role deactivateRole(String id) {
    Role role =
        this.roleRepository
            .findById(id)
            .orElseThrow(
                () ->
                    new EntityRetrieveException(
                        "Role cannot be deactivated, because id " + id + " was not found",
                        HttpStatus.NOT_FOUND,
                        "id"));

    role.setActive(false);
    this.roleRepository.save(role);

    return role;
  }
}
