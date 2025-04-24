package com.bankly.vendura.authentication.security;

import com.bankly.vendura.authentication.roles.model.Role;
import com.bankly.vendura.authentication.user.model.User;
import com.bankly.vendura.utilities.exceptions.CustomAuthenticationException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.*;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

/**
 * Service class responsible for handling JWT operations. Provides methods for extracting,
 * generating, and validating JWT tokens.
 */
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class JWTService {

  private static final Logger LOGGER = LoggerFactory.getLogger(JWTService.class);

  @Value("${spring.jwt.secret}")
  private String jwtSecret;

  @Value("${spring.jwt.expiration}")
  private Long jwtExpiration;

  /**
   * Extracts the JWT from the Authorization header of the HTTP request.
   *
   * @param request the HTTP request containing the Authorization header
   * @return the JWT token, or null if not present or invalid
   */
  public String getJwtFromHeader(HttpServletRequest request) {
    String bearerToken = request.getHeader("Authorization");
    LOGGER.debug("Authorization Header: {}", bearerToken);
    if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
      return bearerToken.substring(7);
    }
    return null;
  }

  /**
   * Generates a JWT token for the provided user details.
   *
   * @param user the user details to include in the JWT
   * @return the generated JWT token
   */
  public String generateToken(User user) {
    final Map<String, Object> claims = new HashMap<>();

    claims.put("userId", user.getId());

    Set<Role> roles = user.getRoles();

    claims.put("roles", roles);

    claims.put("enabled", user.isEnabled());
    claims.put("locked", user.isLocked());

    claims.put("firstname", user.getFirstName());
    claims.put("lastname", user.getLastName());
    claims.put("email", user.getEmail());

    return Jwts.builder()
        .claims(claims)
        .subject(user.getUsername())
        .issuedAt(new Date())
        .expiration(new Date(System.currentTimeMillis() + this.jwtExpiration))
        .signWith(key())
        .compact();
  }

  public User extractUserFromJWTOffline(String token) {
    LOGGER.debug("Extracting user from JWT: {}", token);

    Claims claims =
        Jwts.parser().verifyWith((SecretKey) key()).build().parseSignedClaims(token).getPayload();

    String userId = claims.get("userId", String.class);
    LOGGER.debug("User ID: {}", userId);

    String username = claims.getSubject();
    LOGGER.debug("Username: {}", username);

    String firstname = claims.get("firstname", String.class);
    String lastname = claims.get("lastname", String.class);
    String email = claims.get("email", String.class);

    List<Map<String, Object>> rolesData = claims.get("roles", List.class);
    LOGGER.debug("Roles Data: {}", rolesData);

    Set<Role> roles = new HashSet<>();

    if (rolesData != null) {
      for (Map<String, Object> rolesDatum : rolesData) {
        Role role = new Role();
        role.setId((String) rolesDatum.get("id"));
        role.setName((String) rolesDatum.get("name"));
        role.setActive((Boolean) rolesDatum.get("active"));
        roles.add(role);
      }
    }

    boolean enabled = claims.get("enabled", Boolean.class);
    boolean locked = claims.get("locked", Boolean.class);

    return new User(userId, username, null, firstname, lastname, email, roles, enabled, locked);
  }

  /**
   * Extracts the username from the JWT token.
   *
   * @param token the JWT token
   * @return the username extracted from the token
   */
  public String getUsernameFromToken(String token) {
    return Jwts.parser()
        .verifyWith((SecretKey) key())
        .build()
        .parseSignedClaims(token)
        .getPayload()
        .getSubject();
  }

  /**
   * Generates a signing key for the JWT using the configured secret. This is necessary, because the
   * jwtSecret property will be loaded on construction of the service class
   *
   * @return the signing key
   */
  private Key key() {
    return Keys.hmacShaKeyFor(Decoders.BASE64.decode(this.jwtSecret));
  }

  /**
   * Validates the provided JWT token.
   *
   * @param token the JWT token to validate
   * @throws BadCredentialsException in case of failure
   */
  public void validateToken(String token) {
    try {
      Jwts.parser().verifyWith((SecretKey) key()).build().parseSignedClaims(token);
    } catch (MalformedJwtException exception) {
      LOGGER.error("Invalid JWT token (malformed): {}", exception.getMessage());
      throw new BadCredentialsException("Invalid token", exception);
    } catch (ExpiredJwtException exception) {
      LOGGER.error("JWT token is expired: {}", exception.getMessage());
      throw new BadCredentialsException("Token expired", exception);
    } catch (UnsupportedJwtException exception) {
      LOGGER.error("JWT token is unsupported {}", exception.getMessage());
      throw new BadCredentialsException("Invalid token", exception);
    } catch (IllegalArgumentException exception) {
      LOGGER.error("JWT claims string is empty: {}", exception.getMessage());
      throw new BadCredentialsException("Invalid token", exception);
    } catch (Exception exception) {
      LOGGER.error(
          "Unexpected exception: {}, Message: {}",
          exception.getClass().getSimpleName(),
          exception.getMessage());
      throw new CustomAuthenticationException(
          "Unexpected exception: "
              + exception.getClass().getSimpleName()
              + ", Message: "
              + exception.getMessage(),
          exception,
          HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
