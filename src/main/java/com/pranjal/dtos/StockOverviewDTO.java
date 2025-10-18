package com.pranjal.dtos;

public record StockOverviewDTO(
        String symbol,
        String name,
        String description,
        String exchange,
        String country,
        String officialSite,
        String sector
) {

}
