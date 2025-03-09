package com.bankly.vendura.authentication.roles.controller;

import com.bankly.vendura.authentication.roles.RoleService;
import com.bankly.vendura.authentication.roles.model.IRole;
import com.bankly.vendura.authentication.roles.model.RoleDTO;
import com.bankly.vendura.utilities.exceptions.EntityCreationException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/role")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class RoleControllerV1 {

  private final RoleService roleService;

  @PreAuthorize("hasRole('ADMIN')")
  @RequestMapping(value = "/create", method = RequestMethod.POST)
  public ResponseEntity<?> createRole(@RequestBody RoleDTO roleDTO) {
    IRole role = null;

    role = this.roleService.createRole(roleDTO.getName());

    return ResponseEntity.ok(RoleDTO.fromRole(role));
  }
}
