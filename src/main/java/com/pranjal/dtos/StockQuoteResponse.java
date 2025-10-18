package com.pranjal.dtos;

import lombok.Builder;

@Builder
public record StockQuoteResponse(
        String symbol,
        double price
) {
}
