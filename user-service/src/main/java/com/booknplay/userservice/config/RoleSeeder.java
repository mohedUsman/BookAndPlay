//package com.booknplay.userservice.config;
//
//import com.booknplay.userservice.entity.Role;
//import com.booknplay.userservice.repository.RoleRepository;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//
//import java.util.Arrays;
//import java.util.List;
//
//@Slf4j
//@Component
//@RequiredArgsConstructor
////@Profile({"default", "local", "dev"}) // optional: limit to certain profiles
//public class RoleSeeder implements CommandLineRunner {
//
//    private final RoleRepository roleRepository;
//
//    @Override
//    public void run(String... args) {
//        List<String> requiredRoles = Arrays.asList("ROLE_USER", "ROLE_ADMIN");
//
//        for (String roleName : requiredRoles) {
//            roleRepository.findByName(roleName).ifPresentOrElse(
//                    existing -> log.info("Role '{}' already exists with id={}", roleName, existing.getId()),
//                    () -> {
//                        Role role = new Role();
//                        role.setName(roleName);
//                        Role saved = roleRepository.save(role);
//                        log.info("Created role '{}' with id={}", roleName, saved.getId());
//                    }
//            );
//        }
//    }
//}
