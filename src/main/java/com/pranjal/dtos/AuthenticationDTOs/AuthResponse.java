package com.pranjal.dtos.AuthenticationDTOs;

public record AuthResponse(
        String email,
        String token,
        String name,
        Double balance
) {
}
