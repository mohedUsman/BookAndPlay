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

import com.booknplay.userservice.dto.PasswordChangeDto;
import com.booknplay.userservice.dto.UserDto;
import com.booknplay.userservice.entity.User;
import com.booknplay.userservice.service.UserService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser(@RequestHeader("X-User-Email") String email) { // CHANGE
        User user = userService.getUserByEmail(email);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        User user = userService.getUserByEmail(email);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/me")
    @Transactional
    public ResponseEntity<String> updateCurrentUser(@Valid @RequestBody UserDto request,
                                                    @RequestHeader("X-User-Email") String email) { // CHANGE
        String result = userService.updateCurrentUser(request, () -> email);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/me/password")
    @Transactional
    public ResponseEntity<String> changePassword(@Valid @RequestBody PasswordChangeDto dto,
                                                 @RequestHeader("X-User-Email") String email) { // CHANGE
        String result = userService.changePassword(dto, () -> email);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/me")
    @Transactional
    public ResponseEntity<String> deleteCurrentUser(@RequestHeader("X-User-Email") String email) { // CHANGE
        String result = userService.deleteCurrentUser(() -> email);
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok(result);
    }
}
