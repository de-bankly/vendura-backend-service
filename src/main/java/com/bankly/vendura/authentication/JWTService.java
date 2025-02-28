package com.bankly.vendura.authentication;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.*;
import java.util.stream.Collectors;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
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
   * @param userDetails the user details to include in the JWT
   * @return the generated JWT token
   */
  public String generateToken(UserDetails userDetails) {

    final Map<String, Object> claims = new HashMap<>();
    List<String> roles =
        userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();

    claims.put("roles", roles);

    return Jwts.builder()
        .claims(claims)
        .subject(userDetails.getUsername())
        .issuedAt(new Date())
        .expiration(new Date(System.currentTimeMillis() + this.jwtExpiration))
        .signWith(key())
        .compact();
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
   * @return true if the token is valid, false otherwise
   */
  public boolean validateToken(String token) {
    try {
      Jwts.parser().verifyWith((SecretKey) key()).build().parseSignedClaims(token);
      return true;
    } catch (MalformedJwtException exception) {
      LOGGER.error("Invalid JWT token: {}", exception.getMessage());
    } catch (ExpiredJwtException exception) {
      LOGGER.error("JWT token is expired: {}", exception.getMessage());
    } catch (UnsupportedJwtException exception) {
      LOGGER.error("JWT token is unsupported {}", exception.getMessage());
    } catch (IllegalArgumentException exception) {
      LOGGER.error("JWT claims string is empty: {}", exception.getMessage());
    }
    return false;
  }

  public List<SimpleGrantedAuthority> getAuthoritiesFromToken(String token) {
    Claims claims =
        Jwts.parser().verifyWith((SecretKey) key()).build().parseSignedClaims(token).getPayload();

    List<String> roles = claims.get("roles", List.class);

    if (roles == null) {
      return Collections.emptyList();
    }

    return roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
  }
}
