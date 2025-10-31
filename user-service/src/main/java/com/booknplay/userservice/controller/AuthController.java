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
import lombok.RequiredArgsConstructor;
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

    // CHANGE: removed direct dependencies (AuthenticationManager, UserRepository, RoleRepository, PasswordEncoder, JwtService)
    // CHANGE: inject AuthService to delegate business logic
    private final AuthService authService;

    @Operation(summary = "Register a new user")
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserDto request) {
        // CHANGE: delegate to service, keep same response strings
        String result = authService.register(request);
        if ("User already exists".equals(result)) {
            return ResponseEntity.badRequest().body(result); // preserve previous 400 for duplicate
        }
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Login with email and password")
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request) {
        // CHANGE: delegate to service, returns JWT token string
        String token = authService.login(request);
        return ResponseEntity.ok(token);
    }
}
