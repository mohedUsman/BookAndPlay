package com.booknplay.userservice.service;

import com.booknplay.userservice.dto.LoginRequest;
import com.booknplay.userservice.dto.UserDto;

public interface AuthService {

    String register(UserDto request); // returns message same as controller did
    String login(LoginRequest request); // returns token string same as controller did
}