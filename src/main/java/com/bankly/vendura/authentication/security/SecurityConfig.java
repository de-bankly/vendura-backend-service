package com.bankly.vendura.authentication.security;

import com.bankly.vendura.authentication.user.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;

/**
 * This class handles the Spring Web Security filter chain and provides basic Beans for the
 * authentication flow
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class SecurityConfig {

  private final UserDetailsService userDetailsService;
  private final AuthEntryPointJWT authEntryPointJWT;
  private final AuthTokenFilter authTokenFilter;

  /**
   * This method configures the default security filter chain for the application.
   *
   * @param http the {@link HttpSecurity} object used to configure security settings
   * @return the configured {@link SecurityFilterChain}
   * @throws Exception if an error occurs during the configuration of the security filter chain
   */
  @Bean
  public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
    http.addFilterBefore(authTokenFilter, UsernamePasswordAuthenticationFilter.class);
    http.authorizeHttpRequests(
        authorizeRequests ->
            authorizeRequests
                .requestMatchers("/v1/authentication/**")
                .permitAll() // permitting all requests to /v1/authentication/** without authentication
                .anyRequest()
                .authenticated()); // any other request must be authenticated
    http.sessionManagement(
        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)); // JWT only works with stateless authentication (no sessions)
    http.exceptionHandling(exception -> exception.authenticationEntryPoint(this.authEntryPointJWT)); // Bean AuthEntryPointJWT will handle all exceptions
    //http.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin())); // probably not required
    http.csrf(csrf -> csrf.disable()); // disables requirement of CSRF for POST requests
    http.cors(cors -> cors.disable());

    return http.build();
  }

  /**
   * This method configures the {@link BCryptPasswordEncoder} as the {@link PasswordEncoder} for
   * this project
   *
   * @return the {@link PasswordEncoder} as a Bean
   */
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  /**
   * This method configures the {@link AuthenticationProvider} and ensures the use of the {@link
   * CustomUserDetailsService} and preferred {@link PasswordEncoder}
   *
   * @return the {@link AuthenticationProvider} as a Bean
   */
  @Bean
  public AuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
    provider.setUserDetailsService(this.userDetailsService);
    provider.setPasswordEncoder(passwordEncoder());
    return provider;
  }

  /**
   * This method configures the {@link AuthenticationManager} for this project from the {@link
   * AuthenticationConfiguration}
   *
   * @param configuration the {@link AuthenticationConfiguration} used to configure the
   *     authentication manager
   * @return the configured {@link AuthenticationManager}
   * @throws Exception if an error occurs while retrieving the authentication manager
   */
  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration)
      throws Exception {
    return configuration.getAuthenticationManager();
  }
}
