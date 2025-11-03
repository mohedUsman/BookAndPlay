package com.booknplay.payment_services.dto;

import lombok.Data;

import java.util.List;

@Data
public class UserDto {
    private Long id;
    private String email;
    private String name;
    private List<RoleDto> roles;
}