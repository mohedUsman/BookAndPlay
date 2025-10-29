package com.booknplay.userservice.controller;

import com.booknplay.userservice.dto.LoginRequest;
import com.booknplay.userservice.dto.UserDto;
import com.booknplay.userservice.entity.Role;
import com.booknplay.userservice.entity.User;
import com.booknplay.userservice.repository.RoleRepository;
import com.booknplay.userservice.repository.UserRepository;
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

@Tag(name="Authentication APIs", description = "User registration and login")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Operation(summary = "Register a new user")
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserDto request){
        if (userRepository.findByEmail(request.getEmail()).isPresent()){
            return ResponseEntity.badRequest().body("User already exists");
        }
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        Set<Role> roles = new HashSet<>();
        for (String roleName: request.getRoles()){
            Role role = roleRepository.findByName(roleName).orElseThrow(() -> new RuntimeException("Role not found: "+ roleName));
            roles.add(role);
        }
        user.setRoles(roles);
        userRepository.save(user);
        return ResponseEntity.ok("User registered successfully");

    }

    @Operation(summary = "Login with email and password")
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request){
        Authentication authentication = authenticationManager.authenticate
                (new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtService.generateToken(authentication);
        return ResponseEntity.ok(token);
    }

}
