package com.bankly.vendura.authentication.controller;

import com.bankly.vendura.authentication.controller.models.Login;
import com.bankly.vendura.authentication.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/authentication")
public class AuthenticationControllerV1 {

  private final UserService userService;

  @RequestMapping(value = "/authenticate", method = RequestMethod.POST)
  public ResponseEntity<?> authenticate(@RequestBody Login.Request request) {
    return ResponseEntity.ok(this.userService.authenticate(request));
  }
}
