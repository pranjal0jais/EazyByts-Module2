package com.pranjal.dtos.AlphaVantageDTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record AlphaVantageNewsResponse(
        List<Feed> feed
) {
    public record Feed(
            String title,
            String url,
            @JsonProperty("time_published")
            String publishedAt,
            String summary,
            @JsonProperty("banner_image")
            String image,
            String source,
            @JsonProperty("overall_sentiment_label")
            String sentiment,
            @JsonProperty("ticker_sentiment")
            List<TickerSentiment> tickerSentiment
    ){
        public record TickerSentiment(
                String ticker,
                @JsonProperty("ticker_sentiment_label")
                String sentiment
        ){}
    }
}
