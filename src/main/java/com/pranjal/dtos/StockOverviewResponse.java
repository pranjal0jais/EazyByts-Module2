package com.pranjal.dtos;

public record StockOverviewResponse(
        String symbol,
        String name,
        String description,
        String exchange,
        String country,
        String officialSite,
        String sector
) {

}
