package com.booknplay.userservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordChangeDto {
    @NotBlank
    private String currentPassword;

    @NotBlank
    @Size(min = 6)
    private String newPassword;

    @NotBlank
    private String confirmPassword;
}
