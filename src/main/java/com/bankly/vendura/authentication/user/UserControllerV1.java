package com.bankly.vendura.authentication.user;

import com.bankly.vendura.authentication.user.model.UserDTO;
import com.bankly.vendura.authentication.user.model.UserFactory;
import com.bankly.vendura.authentication.user.model.UserRepository;
import com.bankly.vendura.utilities.exceptions.EntityRetrieveException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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

  /**
   * Retrieve user details of the user whose authentication was used for the request
   *
   * @param authentication automatically injected by Spring, authentication object of user
   * @return currently authenticated users DTO
   */
  @GetMapping("/me")
  public ResponseEntity<?> getAuthenticatedUser(Authentication authentication) {
    return ResponseEntity.ok()
        .body(
            UserFactory.toDTO(
                this.userRepository
                    .findUserByUsername(authentication.getName())
                    .orElseThrow(
                        () ->
                            new EntityRetrieveException(
                                "User with name " + authentication.getName() + " not found",
                                HttpStatus.NOT_FOUND,
                                authentication.getName()))));
  }

  /**
   * Retrieve all users pageable
   *
   * @param pageable pagination information injected by Spring
   * @return page of all users DTOs respecting the given pagination
   */
  @GetMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> getAllUsersPageable(Pageable pageable) {
    return ResponseEntity.ok().body(this.userRepository.findAll(pageable).map(UserFactory::toDTO));
  }

  /**
   * Retrieve a user with a specific ID
   *
   * @param id of the user to retrieve
   * @return DTO of the user with the given ID
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> getUserById(@PathVariable("id") String id) {
    return ResponseEntity.ok(
        UserFactory.toDTO(
            this.userRepository
                .findById(id)
                .orElseThrow(
                    () ->
                        new EntityRetrieveException(
                            "User with id " + id + " not found", HttpStatus.NOT_FOUND, id))));
  }

  /**
   * Create a user
   *
   * @param userDTO details object for the information of the user to be created
   * @return DTO of the created user
   */
  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> createUser(@RequestBody UserDTO userDTO) {
    return ResponseEntity.ok().body(UserFactory.toDTO(this.userService.createUser(userDTO)));
  }

  /**
   * Update user
   *
   * @param userDTO details to be changed (all others should be null)
   * @param id of the user to be updated
   * @return
   */
  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> updateUser(@RequestBody UserDTO userDTO, @PathVariable("id") String id) {
    return ResponseEntity.ok(UserFactory.toDTO(this.userService.updateUser(id, userDTO)));
  }
}
