package com.booknplay.userservice.service;

import com.booknplay.userservice.dto.LoginRequest;
import com.booknplay.userservice.dto.UserDto;

public interface AuthService {

    String register(UserDto request);
    String login(LoginRequest request);
}