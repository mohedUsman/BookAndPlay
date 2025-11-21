package com.booknplay.gateway.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchange -> exchange
                        // Public endpoints
                        .pathMatchers(
                                "/api/users/login",
                                "/api/users/register",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-ui.html"
                        ).permitAll()
                        // Authenticated users can GET turfs; owners required for modifications
                        .pathMatchers("GET", "/api/turfs/**").authenticated()
                        .pathMatchers("POST", "/api/turfs/**").hasAuthority("ROLE_OWNER")
                        .pathMatchers("PUT", "/api/turfs/**").hasAuthority("ROLE_OWNER")
                        .pathMatchers("DELETE", "/api/turfs/**").hasAuthority("ROLE_OWNER")
                        // Authenticated access to user endpoints beyond login/register
                        .pathMatchers("/api/users/**").authenticated()
                        // Default
                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
                .build();
    }
}