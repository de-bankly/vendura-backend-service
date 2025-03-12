package com.bankly.vendura.authentication.user.controller;

import com.bankly.vendura.authentication.roles.model.Role;
import com.bankly.vendura.authentication.roles.model.RoleDTO;
import com.bankly.vendura.authentication.user.UserService;
import com.bankly.vendura.authentication.user.model.User;
import com.bankly.vendura.authentication.user.model.UserDTO;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.bankly.vendura.authentication.user.model.UserRepository;
import com.bankly.vendura.utilities.exceptions.EntityUpdateException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/user")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserControllerV1 {

  private final UserService userService;
  private final UserRepository userRepository;

  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping
  public ResponseEntity<?> createUser(@RequestBody UserDTO userDTO) {
    User user =
        this.userService.createUser(
            userDTO.getUsername(),
            userDTO.getPassword(),
            userDTO.getRoles().stream().map(RoleDTO::getName).collect(Collectors.toSet()));

    return ResponseEntity.ok().body(UserDTO.fromUser(user));
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> updateUser(@RequestBody UserDTO userDTO, @PathVariable("id") String id) {
    User user = this.userRepository.findById(id).orElse(null);

    if (user == null) {
      throw new EntityUpdateException("User with ID " + id + " not found", HttpStatus.NOT_FOUND, null);
    }

    if (userDTO.getId() != null) {
      throw new EntityUpdateException("Cannot update user ID", HttpStatus.UNPROCESSABLE_ENTITY, "id");
    }

    if (userDTO.getUsername() != null && !user.getUsername().equals(userDTO.getUsername())) {
      Optional<User> existing = this.userService.findUserByUsername(userDTO.getUsername());

      if (existing.isPresent()) {
        throw new EntityUpdateException("Username already exists", HttpStatus.CONFLICT, "name");
      }
    }


    if (userDTO.getUsername() != null && !userDTO.getUsername().equals(user.getUsername())) {
      user.setUsername(userDTO.getUsername());
    }

    if (userDTO.getActive() != null && userDTO.getActive() != user.getActive()) {
      user.setActive(userDTO.getActive());
    }

    this.userRepository.save(user);

    return ResponseEntity.ok(UserDTO.fromUser(user));
  }

  @RequestMapping(value = "/own", method = RequestMethod.GET)
  public ResponseEntity<?> getOwnUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String username = authentication.getName();

    User user = this.userService.getUser(username);
    return ResponseEntity.ok().body(UserDTO.fromUser(user));
  }

  @GetMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> getAllUsers() {
    List<User> allUsers = this.userRepository.findAll();
    List<UserDTO> allUserDtos = allUsers.stream().map(UserDTO::fromUser).toList();
    return ResponseEntity.ok().body(allUserDtos);
  }
}