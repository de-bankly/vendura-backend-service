package com.bankly.vendura.authentication;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Filter that checks the JWT token in each request. Validates the token and sets the user
 * authentication in the security context.
 */
@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class AuthTokenFilter extends OncePerRequestFilter {

  private static final Logger LOGGER = LoggerFactory.getLogger(AuthTokenFilter.class);

  private final JWTService jwtService;
  private final UserDetailsService userDetailsService;

  /**
   * This method processes the incoming request, checks the JWT token, validates it, and sets the
   * authentication if the token is valid.
   *
   * @param request the HTTP request
   * @param response the HTTP response
   * @param filterChain the filter chain for further request processing
   * @throws ServletException if an error occurs during request processing
   * @throws IOException if an input/output error occurs during request processing
   */
  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    LOGGER.debug("AuthTokenFilter was invoked for URI: {}", request.getRequestURI());

    try {
      String token = parseToken(request); // Reading token from HTTP request
      LOGGER.debug("Authentication with token: {}", token);

      if (token != null && this.jwtService.validateToken(token)) { // Token is not null and valid

        PreAuthenticatedAuthenticationToken authentication =
            new PreAuthenticatedAuthenticationToken(
                this.jwtService.getUsernameFromToken(token),
                token,
                this.jwtService.getAuthoritiesFromToken(
                    token)); // Authentication for Spring with external token, username and
                             // authorities are parsed from the token by JWTService

        LOGGER.debug(
            "Token validated tor user {} with authorities {}",
            authentication.getPrincipal(),
            authentication.getAuthorities());
        LOGGER.debug("Roles from JWT: {}", authentication.getAuthorities());

        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authentication);
      }
    } catch (Exception exception) {
      LOGGER.error("Cannot set user authentication: {}", exception.getMessage());
      LOGGER.error("Exception", exception);
    }

    filterChain.doFilter(request, response);
  }

  /**
   * Parses the JWT token from the Authorization header in the HTTP request.
   *
   * @param request the HTTP request
   * @return the extracted token or null if no token is found
   */
  private String parseToken(HttpServletRequest request) {
    String token = this.jwtService.getJwtFromHeader(request);
    LOGGER.debug("AuthTokenFilter.java: {}", token);
    return token;
  }

}
