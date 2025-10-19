package com.pranjal.dtos.TradingDTOs;

import com.pranjal.dtos.TransactionType;
import lombok.Builder;

@Builder
public record StockSellResponse(
        String transactionId,
        TransactionType type,
        String stockSymbol,
        Double pricePerUnit,
        int quantity,
        Double totalAmount,
        String createdAt,
        boolean profit,
        double profitOrLoss
) {
}
