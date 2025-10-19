package com.pranjal.dtos.AlphaVantageDTOs;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AlphaVantageStockOverviewResponse(
        @JsonProperty("Symbol")
        String symbol,
        @JsonProperty("Name")
        String name,
        @JsonProperty("Description")
        String description,
        @JsonProperty("Exchange")
        String exchange,
        @JsonProperty("Country")
        String country,
        @JsonProperty("OfficialSite")
        String officialSite,
        @JsonProperty("Sector")
        String sector
) {
}
