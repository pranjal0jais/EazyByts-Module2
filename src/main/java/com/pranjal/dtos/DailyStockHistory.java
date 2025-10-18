package com.pranjal.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

public record DailyStockHistory(
        String date,
        Double open,
        Double high,
        Double low,
        Double close,
        Long volume
) {
}
