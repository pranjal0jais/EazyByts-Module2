package com.pranjal.dtos;


import lombok.Builder;

import java.util.List;

@Builder
public record PortfolioResponse(
        int totalStocks,
        Double totalInvestedValue,
        Double totalCurrentValue,
        Double totalProfitLoss,
        List<StockSummary> stockSummary
) {
    @Builder
    public record StockSummary(
            String symbol,
            int quantity,
            Double investedValue,
            Double currentValue,
            Double profitLoss
    ){
    }
}
