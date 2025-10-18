package com.pranjal.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

public record AlphaVantageResponse(
        @JsonProperty("Global Quote") GlobalQuote globalQuote
) {
    public record GlobalQuote(@JsonProperty("01. symbol") String symbol,
                              @JsonProperty("05. price") String price) {
    }
}
