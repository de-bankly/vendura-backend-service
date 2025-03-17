package com.bankly.vendura.authentication.controller;

import com.bankly.vendura.authentication.controller.models.Login;
import com.bankly.vendura.authentication.security.JWTService;
import java.util.List;

import com.bankly.vendura.authentication.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/authentication")
public class AuthenticationControllerV1 {

  private final JWTService jwtService;
  private final AuthenticationManager authenticationManager;
  private final UserService userService;

  @RequestMapping(value = "/authenticate", method = RequestMethod.POST)
  public ResponseEntity<?> authenticate(@RequestBody Login.Request request) {
    return ResponseEntity.ok(this.userService.authenticate(request));
  }
}
