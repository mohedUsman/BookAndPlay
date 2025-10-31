package com.booknplay.userservice.serviceimpl;

import com.booknplay.userservice.dto.LoginRequest;
import com.booknplay.userservice.dto.UserDto;
import com.booknplay.userservice.entity.Role;
import com.booknplay.userservice.entity.User;
import com.booknplay.userservice.repository.RoleRepository;
import com.booknplay.userservice.repository.UserRepository;
import com.booknplay.userservice.service.AuthService;
import com.booknplay.userservice.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    // CHANGE: moved dependencies from controller to service implementation
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public String register(UserDto request) {
        // CHANGE: logic moved from AuthController.register into service
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            // Keeping same behavior: return a bad request message (controller maps to body only)
            return "User already exists";
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        Set<Role> roles = new HashSet<>();
        for (String roleName : request.getRoles()) {
            Role role = roleRepository.findByName(roleName)
                    .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));
            roles.add(role);
        }
        user.setRoles(roles);
        userRepository.save(user);

        return "User registered successfully";
    }

    @Override
    public String login(LoginRequest request) {
        // CHANGE: logic moved from AuthController.login into service
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return jwtService.generateToken(authentication);
    }
}