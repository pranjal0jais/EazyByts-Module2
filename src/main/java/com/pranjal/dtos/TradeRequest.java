package com.pranjal.dtos;

public record TradeRequest(
    String stockSymbol,
    int quantity
) {
}
