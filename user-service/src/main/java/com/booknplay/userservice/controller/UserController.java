package com.booknplay.userservice.controller;

import com.booknplay.userservice.dto.PasswordChangeDto;
import com.booknplay.userservice.dto.UserDto;
import com.booknplay.userservice.entity.User;
import com.booknplay.userservice.exception.ResourceNotFoundException;
import com.booknplay.userservice.repository.UserRepository;
import com.booknplay.userservice.service.JwtService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser(Principal principal) {
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return ResponseEntity.ok(user);
    }


    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return ResponseEntity.ok(user);
    }

    @GetMapping("/email/{email}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return ResponseEntity.ok(user);
    }

    @PutMapping("/me")
    //id need to take with token,
    //MDC concept
    @Transactional
    public ResponseEntity<String> updateCurrentUser(
                                             @Valid @RequestBody UserDto request,
                                             Principal principal) {
//        User user = userRepository.findById(id)
//                .orElseThrow(() -> new ResourceNotFoundException("User not found!"))
        if(principal == null || principal.getName() == null) {
            return  ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not authenticated");
        }

        // finding the currently authenticated user by username/email returned by principal.getName()
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not Found"));

        user.setName(request.getName());
        user.setPhone(request.getPhone());

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        userRepository.save(user);
        return ResponseEntity.ok("User updated successfully!");
    }


    @PutMapping("/me/password")
    @Transactional
    public ResponseEntity<String> changePassword(@Valid @RequestBody PasswordChangeDto dto, Principal principal) {
        if (principal == null || principal.getName() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not authenticated");
        }

        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // verify current password
        if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Current password is incorrect");
        }

        // basic new password checks
        if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("New password and confirm password do not match");
        }
        if (dto.getNewPassword().length() < 8) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("New password must be at least 8 characters");
        }

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);

        return ResponseEntity.ok("Password updated");
    }


    @DeleteMapping("/me")
    @Transactional
    public ResponseEntity<String> deleteCurrentUser(Principal principal) {
        if (principal == null || principal.getName() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not authenticated");
        }

        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));


        userRepository.delete(user);

        // Clear any session/security context
        SecurityContextHolder.clearContext();

        return ResponseEntity.ok("Account deleted successfully");
    }


}
