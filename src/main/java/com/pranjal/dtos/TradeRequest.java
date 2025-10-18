package com.pranjal.dtos;

public record TradeRequest(
    String symbol,
    int quantity
) {
}
