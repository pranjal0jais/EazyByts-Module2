package com.pranjal.dtos.AuthenticationDTOs;

public record UserResponse(
        String userId,
        String email,
        String name,
        Double virtualBalance,
        String createdAt
) {
}
