package com.pranjal.dtos;

import java.time.LocalDateTime;

public record ErrorResponse(
        String message,
        LocalDateTime timestamp,
        String status
) {
}
