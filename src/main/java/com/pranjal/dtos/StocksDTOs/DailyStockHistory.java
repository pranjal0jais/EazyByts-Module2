package com.pranjal.dtos.StocksDTOs;

public record DailyStockHistory(
        String date,
        Double open,
        Double high,
        Double low,
        Double close,
        Long volume
) {
}
