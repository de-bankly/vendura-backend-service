package com.bankly.vendura.authentication.roles.controller;

import com.bankly.vendura.authentication.roles.RoleService;
import com.bankly.vendura.authentication.roles.model.Role;
import com.bankly.vendura.authentication.roles.model.RoleDTO;
import com.bankly.vendura.authentication.roles.model.RoleRepository;
import com.bankly.vendura.utilities.exceptions.EntityCreationException;
import com.bankly.vendura.utilities.exceptions.EntityUpdateException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/v1/role")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class RoleControllerV1 {

  private final RoleService roleService;
  private final RoleRepository roleRepository;

  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> createRole(@RequestBody RoleDTO roleDTO) {

    if (!roleDTO.getActive()) {
      throw new EntityCreationException("Inactive role cannot be created", HttpStatus.UNPROCESSABLE_ENTITY, "Role", true);
    }

    Role role = this.roleService.createRole(roleDTO.getName());

    return ResponseEntity.ok(RoleDTO.fromRole(role));
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> updateRole(@RequestBody RoleDTO roleDTO, @PathVariable("id") String id) {
    Role role = this.roleRepository.findById(id).orElse(null);

    if (role == null) {
      throw new EntityUpdateException("Role with ID " + id + " not found", HttpStatus.NOT_FOUND, null);
    }

    if (!roleDTO.getId().equals(role.getId())) {
      throw new EntityUpdateException("Cannot update roles ID", HttpStatus.UNPROCESSABLE_ENTITY, "id");
    }

    if (roleDTO.getName() != null && !role.getName().equals(roleDTO.getName())) {
      Optional<Role> existing = this.roleService.findRoleByName(roleDTO.getName());

      if (existing.isPresent()) {
        throw new EntityUpdateException("Name of role already exists", HttpStatus.CONFLICT, "name");
      }
    }


    if (roleDTO.getName() != null) {
      role.setName(roleDTO.getName());
    }

    if (roleDTO.getActive() != null) {
      role.setActive(roleDTO.getActive());
    }

    this.roleRepository.save(role);

    return ResponseEntity.ok(RoleDTO.fromRole(role));
  }

  @GetMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> getAllRoles() {
    List<Role> allRoles = this.roleRepository.findAll();
    List<RoleDTO> allRoleDtos = allRoles.stream().map(RoleDTO::fromRole).toList();
    return ResponseEntity.ok().body(allRoleDtos);
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> getRole(@PathVariable("id") String id) {
    Optional<Role> roleOptional = this.roleRepository.findById(id);

    if (roleOptional.isPresent()) {
      RoleDTO roleDto = RoleDTO.fromRole(roleOptional.get());
      return ResponseEntity.ok(roleDto);
    }

    return ResponseEntity.notFound().build();
  }

}
