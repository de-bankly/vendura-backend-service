package com.bankly.vendura.authentication.user.controller;

import com.bankly.vendura.authentication.roles.model.RoleDTO;
import com.bankly.vendura.authentication.user.UserService;
import com.bankly.vendura.authentication.user.model.IUser;
import com.bankly.vendura.authentication.user.model.User;
import com.bankly.vendura.authentication.user.model.UserDTO;
import com.bankly.vendura.exceptions.EntityCreationException;
import com.bankly.vendura.exceptions.EntityCreationExceptionResponse;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/user")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserControllerV1 {

  private final UserService userService;

  @PreAuthorize("hasRole('ADMIN')")
  @RequestMapping(value = "/create", method = RequestMethod.POST)
  public ResponseEntity<?> createUser(@RequestBody UserDTO userDTO) {
    IUser user = null;
    try {
      user =
          this.userService.createUser(
              userDTO.getUsername(),
              userDTO.getPassword(),
              userDTO.getRoles().stream().map(RoleDTO::getName).collect(Collectors.toSet()));
    } catch (EntityCreationException e) {
      return ResponseEntity.badRequest()
          .body(
              new EntityCreationExceptionResponse(
                  "/v1/user/create", "Bad Request", e.getMessage(), 400));
    }

    return ResponseEntity.ok().body(UserDTO.fromUser(user));
  }

  @RequestMapping(value = "/own", method = RequestMethod.GET)
  public ResponseEntity<?> getOwnUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String username = authentication.getName();

    IUser user = this.userService.getUser(username);
    return ResponseEntity.ok().body(UserDTO.fromUser(user));
  }

}
