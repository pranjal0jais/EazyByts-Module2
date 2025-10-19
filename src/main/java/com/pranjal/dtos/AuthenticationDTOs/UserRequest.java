package com.pranjal.dtos.AuthenticationDTOs;

public record UserRequest(
        String email,
        String password,
        String name
) {
}
