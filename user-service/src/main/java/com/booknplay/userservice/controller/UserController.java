package com.booknplay.userservice.controller;

import com.booknplay.userservice.dto.PasswordChangeDto;
import com.booknplay.userservice.dto.UserDto;
import com.booknplay.userservice.entity.User;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import com.booknplay.userservice.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser(Principal principal) {
        User user = userService.getCurrentUser(principal);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/email/{email}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        User user = userService.getUserByEmail(email);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/me")
    @Transactional
    public ResponseEntity<String> updateCurrentUser(@Valid @RequestBody UserDto request,
                                                    Principal principal) {
        String result = userService.updateCurrentUser(request, principal);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/me/password")
    @Transactional
    public ResponseEntity<String> changePassword(@Valid @RequestBody PasswordChangeDto dto,
                                                 Principal principal) {
        String result = userService.changePassword(dto, principal);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/me")
    @Transactional
    public ResponseEntity<String> deleteCurrentUser(Principal principal) {
        String result = userService.deleteCurrentUser(principal);

        SecurityContextHolder.clearContext();

        return ResponseEntity.ok(result);
    }
}
