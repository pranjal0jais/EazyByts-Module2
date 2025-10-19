package com.pranjal.dtos.TradingDTOs;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TradeRequest(
    @NotBlank(message = "symbol is required")
    String symbol,
    @NotNull(message = "quantity is required")
    @Min(1)
    int quantity
) {
}
