package com.bankly.vendura.authentication.user.controller;

import com.bankly.vendura.authentication.user.UserService;
import com.bankly.vendura.authentication.user.model.User;
import com.bankly.vendura.authentication.user.model.UserDTO;
import com.bankly.vendura.authentication.user.model.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

  @GetMapping("/me")
  public ResponseEntity<?> getAuthenticatedUser(Authentication authentication) {
    User user = this.userService.getUser(authentication.getName());
    return ResponseEntity.ok().body(UserDTO.fromUser(user));
  }

  @GetMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> getAllUsersPageable(Pageable pageable) {
    Page<User> users = this.userRepository.findAll(pageable);
    return ResponseEntity.ok().body(users.map(UserDTO::fromUser));
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> getUserById(@PathVariable("id") String id) {
    User user = this.userService.getUserById(id);
    return ResponseEntity.ok(UserDTO.fromUser(user));
  }

  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> createUser(@RequestBody UserDTO userDTO) {
    User user = this.userService.createUser(userDTO);
    return ResponseEntity.ok().body(UserDTO.fromUser(user));
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> updateUser(@RequestBody UserDTO userDTO, @PathVariable("id") String id) {
    User user = this.userService.updateUser(id, userDTO);
    return ResponseEntity.ok(UserDTO.fromUser(user));
  }
}
