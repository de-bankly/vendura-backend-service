package com.bankly.vendura.authentication.security;

import com.bankly.vendura.authentication.user.model.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

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
  private final HandlerExceptionResolver handlerExceptionResolver;

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
      HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
      throws ServletException, IOException {

    // This filter will ignore all requests on /v1/authentication and /error, because authentication
    // is not required there
    if (request.getRequestURI().startsWith("/v1/authentication")
        || request.getRequestURI().equalsIgnoreCase("/error")
        || request.getRequestURI().startsWith("/public/")
        || request.getRequestURI().startsWith("/swagger-ui/")
        || request.getRequestURI().equalsIgnoreCase("/favicon.ico")) {

      filterChain.doFilter(request, response); // further processing of the filter chain
      return;
    }

    LOGGER.debug("AuthTokenFilter was invoked for URI: {}", request.getRequestURI());

    String token = parseToken(request); // Reading token from HTTP request
    LOGGER.debug("Authentication with token: {}", token);

    // If token is null or empty, GlobalExceptionHandler will deal with the AuthenticationException
    if (token == null || token.isEmpty()) {
      LOGGER.debug("Invalid/empty token");
      this.handlerExceptionResolver.resolveException(
          request, response, null, new InsufficientAuthenticationException("Invalid token"));
      return;
    }

    try {
      this.jwtService.validateToken(
          token); // Token gets validated here, unchecked exception expected on failure

      User user = this.jwtService.extractUserFromJWTOffline(token);

      PreAuthenticatedAuthenticationToken authentication =
          new PreAuthenticatedAuthenticationToken(
              user, // User is extracted from the token
              token,
              user.getAuthorities()); // Authentication for Spring with external token, username and
      // authorities are parsed from the token by JWTService

      LOGGER.debug(
          "Token validated tor user {} with authorities {}",
          authentication.getPrincipal(),
          authentication.getAuthorities());

      authentication.setDetails(
          new WebAuthenticationDetailsSource()
              .buildDetails(request)); // logging requests details on authentication
      authentication.setAuthenticated(true);

      SecurityContextHolder.getContext().setAuthentication(authentication);

    } catch (Exception exception) {
      LOGGER.error(
          "Authentication failed: {}, processing to ExceptionResolver",
          exception.getClass().getSimpleName());
      this.handlerExceptionResolver.resolveException(
          request, response, null, exception); // Exceptions will be processed further
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
