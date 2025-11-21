package com.booknplay.turf_service.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    // CHANGE: Remove JwtAuthenticationFilter and UserDetailsService use in the chain
    // private final JwtAuthenticationFilter jwtAuthenticationFilter; // REMOVED

    // CHANGE: Gateway trust filter (simplified; can add signature verification similarly to Turf)
    static class GatewayTrustFilter extends OncePerRequestFilter {
        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
                throws ServletException, IOException {

            String path = request.getRequestURI();
            boolean isPublic = path.startsWith("/api/users/login")
                    || path.startsWith("/api/users/register")
                    || path.startsWith("/swagger-ui")
                    || path.startsWith("/v3/api-docs")
                    || path.equals("/swagger-ui.html");

            String caller = request.getHeader("X-Internal-Caller");

            if (!isPublic) {
                if (!"gateway".equals(caller)) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getWriter().write("Forbidden: Requests must come through API Gateway.");
                    return;
                }
                // OPTIONAL: verify X-Internal-Signature and timestamp here as done in Turf
            }
            chain.doFilter(request, response);
        }
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth ->
                        auth.requestMatchers(
                                        "/api/users/login","/api/users/register",
                                        "/swagger-ui/**",
                                        "/v3/api-docs/**",
                                        "/swagger-ui.html"
                                ).permitAll()
                                // CHANGE: Permit all under /api/users/** because trust filter will block non-gateway calls
                                .requestMatchers("/api/users/**").permitAll()
                                .anyRequest().permitAll()
                )
                .sessionManagement(sess-> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // CHANGE: Remove JWT authentication filter
                // .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                // CHANGE: Add trust filter
                .addFilterBefore(new GatewayTrustFilter(), org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception{
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
