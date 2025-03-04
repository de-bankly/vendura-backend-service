package com.bankly.vendura.authentication.controller;

import com.bankly.vendura.authentication.security.JWTService;
import com.bankly.vendura.authentication.controller.models.Login;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/authentication")
@Component("venduraAuthenticationControllerV1")
public class AuthenticationControllerV1 {

  private final JWTService jwtService;
  private final AuthenticationManager authenticationManager;

  @PreAuthorize("hasRole('USER')")
  @RequestMapping("/user")
  public String user() {
    return "pre authrotized role USER";
  }

  @PreAuthorize("hasRole('ADMIN')")
  @RequestMapping("/admin")
  public String admin() {
    return "pre authrotized role ADMIN";
  }

  @RequestMapping("/public")
  public String publicC() {
    return "public";
  }

  @RequestMapping(value = "/authenticate", method = RequestMethod.POST)
  public ResponseEntity<?> authenticate(@RequestBody Login.Request request) {
    Authentication authentication = null;

    authentication =
        this.authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

    SecurityContextHolder.getContext().setAuthentication(authentication);
    UserDetails userDetails = (UserDetails) authentication.getPrincipal();

    String jwtToken = this.jwtService.generateToken(userDetails);
    List<String> roles =
        userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();

    Login.Response response = new Login.Response(jwtToken, userDetails.getUsername(), roles);

    return ResponseEntity.ok(response);
  }
}
