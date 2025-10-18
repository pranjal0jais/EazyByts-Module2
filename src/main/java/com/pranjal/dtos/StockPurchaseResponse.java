package com.pranjal.dtos;

import lombok.Builder;

@Builder
public record StockPurchaseResponse(
        String transactionId,
        TransactionType type,
        String stockSymbol,
        Double pricePerUnit,
        int quantity,
        Double totalAmount,
        String createdAt
) {
}
