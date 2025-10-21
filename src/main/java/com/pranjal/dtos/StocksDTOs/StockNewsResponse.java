package com.pranjal.dtos.StocksDTOs;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record StockNewsResponse(
        String title,
        String url,
        String publishedAt,
        String summary,
        String image,
        String source,
        String sentiment,
        List<TickerSentiment> tickerSentiment
) {
    public record TickerSentiment(
            String ticker,
            String sentiment
    ){}
}