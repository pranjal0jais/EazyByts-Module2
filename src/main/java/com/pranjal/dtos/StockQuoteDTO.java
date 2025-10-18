package com.pranjal.dtos;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Builder
public record StockQuoteDTO(
        String symbol,
        double price
) {
}
