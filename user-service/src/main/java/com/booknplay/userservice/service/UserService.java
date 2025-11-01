package com.booknplay.userservice.service;

import com.booknplay.userservice.dto.PasswordChangeDto;
import com.booknplay.userservice.dto.UserDto;
import com.booknplay.userservice.entity.User;
import jakarta.transaction.Transactional;

import java.security.Principal;

public interface UserService {

    User getCurrentUser(Principal principal);

    User getUserById(Long id);

    User getUserByEmail(String email);

    @Transactional
    String updateCurrentUser(UserDto request, Principal principal);

    @Transactional
    String changePassword(PasswordChangeDto dto, Principal principal);

    @Transactional
    String deleteCurrentUser(Principal principal);
}
