package com.booknplay.userservice.dto;

import lombok.Data;

import java.util.Set;

@Data
public class UserDto {
    private String name;
    private String email;
    private String password;
    private String phone;
    private Set<String> roles;

}
