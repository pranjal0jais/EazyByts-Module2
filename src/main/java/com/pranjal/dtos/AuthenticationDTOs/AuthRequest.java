package com.pranjal.dtos.AuthenticationDTOs;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record AuthRequest(
        @Email(message = "Email is invalid")
        @NotNull(message = "Email is required")
        String email,
        @NotNull(message = "Password is required")
        String password
) {
}
