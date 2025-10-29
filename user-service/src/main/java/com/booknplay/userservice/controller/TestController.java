package com.booknplay.userservice.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SecurityRequirement(name= "bearerAuth")
@RestController
@RequestMapping("/api/test")
public class TestController {
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String adminAccess(){
        return "Hello, Admin!";
    }

    @GetMapping("/user")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    public String userAccess(){
        return "Hello, user!";
    }

    @GetMapping("/all")
    public String publicAccess(){
        return "Public Endpoint!";
    }

}
