package com.booknplay.userservice.controller;

import com.booknplay.userservice.dto.UserDto;
import com.booknplay.userservice.entity.User;
import com.booknplay.userservice.repository.UserRepository;
import com.booknplay.userservice.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.Principal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private JwtService jwtService;
    private UserController userController;

    @BeforeEach
    public void setup(){
        userRepository = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        jwtService = mock(JwtService.class);
        userController = new UserController(userRepository, passwordEncoder, jwtService);
    }

    @Test
    public void testUpdateUser(){
        User user = new User();
        user.setId(1L);
        user.setEmail("testuser@gmail.com");
        UserDto updatedRequest = new UserDto();
        updatedRequest.setName("new name");
        updatedRequest.setPhone("123456");
        Principal principal = ()-> "testuser@gmail.com";
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(Mockito.any(User.class))).thenReturn(user);

        ResponseEntity<String> response = userController.updateUser(1L, updatedRequest, principal);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("User updated successfully!", response.getBody());
    }

    @Test
    public void testDeleteUser() {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");

        Principal principal = () -> "test@example.com";

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        ResponseEntity<String> response = userController.deleteUser(1L, principal);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("User deleted successfully!", response.getBody());
    }

    @Test
    public void testGetCurrentUser() {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");

        Principal principal = () -> "test@example.com";

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        ResponseEntity<User> response = userController.getCurrentUser(principal);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(user, response.getBody());
    }

}
