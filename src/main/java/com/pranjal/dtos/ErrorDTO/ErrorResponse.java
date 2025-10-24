package com.pranjal.dtos.ErrorDTO;

import java.time.LocalDateTime;

public record ErrorResponse(
        String message,
        LocalDateTime timestamp,
        String status
) {
}
