package com.bankly.vendura.authentication.roles.controller;

import com.bankly.vendura.authentication.roles.RoleService;
import com.bankly.vendura.authentication.roles.model.RoleDTO;
import com.bankly.vendura.authentication.roles.model.RoleRepository;
import com.bankly.vendura.utilities.exceptions.EntityRetrieveException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/role")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class RoleControllerV1 {

  private final RoleService roleService;
  private final RoleRepository roleRepository;

  /**
   * Retrieves all roles from the database as page
   *
   * @param pageable pagination information
   * @return Page of all roles DTOs respecting the pagination information
   */
  @GetMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> getAllRoles(Pageable pageable) {
    return ResponseEntity.ok(this.roleRepository.findAll(pageable));
  }

  /**
   * Retrieves a specific role
   *
   * @param id requested roles ID
   * @return role as DTO
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> getRole(@PathVariable("id") String id) {
    return ResponseEntity.ok(
        RoleDTO.fromRole(
            this.roleRepository
                .findById(id)
                .orElseThrow(
                    () ->
                        new EntityRetrieveException(
                            "Role with id" + id + " not found", HttpStatus.NOT_FOUND, id))));
  }

  /**
   * Creation of roles
   *
   * @param roleDTO role information for creation
   * @return
   */
  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> createRole(@RequestBody RoleDTO roleDTO) {
    return ResponseEntity.ok(RoleDTO.fromRole(this.roleService.createRole(roleDTO)));
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> updateRole(@RequestBody RoleDTO roleDTO, @PathVariable("id") String id) {
    return ResponseEntity.ok(RoleDTO.fromRole(this.roleService.updateRole(id, roleDTO)));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> deactivateRole(@PathVariable("id") String id) {
    return ResponseEntity.ok(RoleDTO.fromRole(this.roleService.deactivateRole(id)));
  }
}
