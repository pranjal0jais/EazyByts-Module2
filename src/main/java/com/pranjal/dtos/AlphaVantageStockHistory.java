package com.pranjal.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;
public record AlphaVantageStockHistory(
        @JsonProperty("Meta Data") MetaData metaData,
        @JsonProperty("Time Series (Daily)")Map<String, DailyStockData> timeSeries
        ) {
    public record MetaData(
            @JsonProperty("2. Symbol") String symbol
    ){}

    public record DailyStockData(
            @JsonProperty("1. open") String open,
            @JsonProperty("2. high") String high,
            @JsonProperty("3. low") String low,
            @JsonProperty("4. close") String close,
            @JsonProperty("5. volume") String volume
    ){}
}
