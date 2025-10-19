package com.pranjal.dtos.StocksDTOs;

import lombok.Builder;

@Builder
public record StockQuoteResponse(
        String symbol,
        double price
) {
}
