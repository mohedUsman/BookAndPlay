package com.booknplay.userservice.serviceimpl;

import com.booknplay.userservice.dto.LoginRequest;
import com.booknplay.userservice.dto.UserDto;
import com.booknplay.userservice.entity.Role;
import com.booknplay.userservice.entity.User;
import com.booknplay.userservice.exception.ConflictException;
import com.booknplay.userservice.exception.ResourceNotFoundException;
import com.booknplay.userservice.exception.UnauthorizedException;
import com.booknplay.userservice.repository.RoleRepository;
import com.booknplay.userservice.repository.UserRepository;
import com.booknplay.userservice.service.AuthService;
import com.booknplay.userservice.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final String ROLE_ADMIN = "ROLE_ADMIN"; // CHANGE: constants for clarity
    private static final String ROLE_OWNER = "ROLE_OWNER";

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public String register(UserDto request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ConflictException("User already exists");
        }

       Set<String> requestedRoleName = request.getRoles() != null ? request.getRoles() : new HashSet<>();

        if(requestedRoleName.isEmpty()){
            requestedRoleName = new HashSet<>();
           requestedRoleName.add("ROLE_USER");
        }

        if (requestedRoleName.contains(ROLE_ADMIN)){
           Authentication requesterAuth = SecurityContextHolder.getContext().getAuthentication();
           if(requesterAuth == null || !requesterAuth.isAuthenticated()){
               throw  new UnauthorizedException("Only users with ROLE_OWNER can assign privileged roles");
           }
            boolean requesterIsOwner = requesterAuth.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .anyMatch(ROLE_OWNER::equals);

            if (!requesterIsOwner) {
                throw new UnauthorizedException("Only users with ROLE_OWNER can assign privileged roles");
            }
        }


        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        Set<Role> roles = new HashSet<>();
        if (request.getRoles() != null) {
            for (String roleName : request.getRoles()) {
                Role role = roleRepository.findByName(roleName)
                        .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + roleName));
                roles.add(role);
            }
        }
        user.setRoles(roles);
        userRepository.save(user);

        return "User registered successfully";
    }

    @Override
    public String login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return jwtService.generateToken(authentication);
    }
}