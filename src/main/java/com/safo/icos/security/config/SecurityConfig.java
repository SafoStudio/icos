package com.safo.icos.security.config;

import com.safo.icos.security.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security configuration for the application.
 * <p>
 * Configures a stateless security model with no CSRF protection,
 * public access to GraphQL and public API endpoints,
 * and JWT-based authentication for all other requests.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final JwtAuthenticationFilter jwtAuthenticationFilter;

  /**
   * Builds the security filter chain.
   * <ul>
   *   <li>CSRF disabled — safe for stateless JWT APIs.</li>
   *   <li>Session management set to stateless — no HTTP sessions created.</li>
   *   <li>{@code /graphql} and {@code /api/public/**} are publicly accessible.</li>
   *   <li>All other requests require authentication.</li>
   *   <li>JWT filter runs before {@link UsernamePasswordAuthenticationFilter}.</li>
   * </ul>
   *
   * @param http the HTTP security builder
   * @return the configured security filter chain
   */
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session ->
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/graphql", "/api/public/**").permitAll()
                    .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter,
                    UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }
}
