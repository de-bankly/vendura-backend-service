package com.bankly.vendura.authentication.user.controller;

import com.bankly.vendura.authentication.user.UserService;
import com.bankly.vendura.authentication.user.model.User;
import com.bankly.vendura.authentication.user.model.UserDTO;
import com.bankly.vendura.authentication.user.model.UserRepository;
import com.bankly.vendura.utilities.exceptions.EntityRetrieveException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/user")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserControllerV1 {

  private final UserService userService;
  private final UserRepository userRepository;

  @PreAuthorize("hasRole('ADMIN')")
  @RequestMapping(value = "/create", method = RequestMethod.POST)
  public ResponseEntity<?> createUser(@RequestBody UserDTO userDTO) {
    User user = this.userService.createUser(userDTO);
    return ResponseEntity.ok().body(UserDTO.fromUser(user));
  }

  @GetMapping("/me")
  public ResponseEntity<?> getUser(Authentication authentication) {
    User user = this.userService.getUser(authentication.getName());
    return ResponseEntity.ok().body(UserDTO.fromUser(user));
  }

  @GetMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> getAllUsers(Pageable pageable) {
    Page<User> users = this.userRepository.findAll(pageable);
    return ResponseEntity.ok().body(users.map(UserDTO::fromUser));
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> getUserById(@PathVariable("id") String id) {
    return ResponseEntity.ok(
        UserDTO.fromUser(
            this.userRepository
                .findById(id)
                .orElseThrow(
                    () ->
                        new EntityRetrieveException(
                            "User with id " + id + " not found", HttpStatus.NOT_FOUND, id))));
  }
}
