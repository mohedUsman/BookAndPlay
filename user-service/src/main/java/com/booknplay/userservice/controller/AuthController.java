package com.booknplay.userservice.controller;

import com.booknplay.userservice.dto.LoginRequest;
import com.booknplay.userservice.dto.UserDto;
import com.booknplay.userservice.entity.Role;
import com.booknplay.userservice.entity.User;
import com.booknplay.userservice.repository.RoleRepository;
import com.booknplay.userservice.repository.UserRepository;
import com.booknplay.userservice.service.AuthService;
import com.booknplay.userservice.service.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.Set;

@Tag(name = "Authentication APIs", description = "User registration and login")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;

    @Operation(summary = "Register a new user",
            description = "User Registration through this api, If no role is provided default ROLE_USER it will take" +
                    "Only user with TURF_OWNER can create the ROLE_ADMIN")
    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody UserDto request) {
        logger.info("Register API called with email: {}", request.getEmail());
        String result = authService.register(request);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Login with email and password")
    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody LoginRequest request) {
        String token = authService.login(request);
        logger.info("User registration successful for email: {}", request.getEmail());

        return ResponseEntity.ok(token);
    }
}
