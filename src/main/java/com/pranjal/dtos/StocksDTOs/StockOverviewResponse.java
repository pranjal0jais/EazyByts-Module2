package com.pranjal.dtos.StocksDTOs;

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
