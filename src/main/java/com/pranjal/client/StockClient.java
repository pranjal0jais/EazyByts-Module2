package com.pranjal.client;

import com.pranjal.dtos.AlphaVantageDTOs.AlphaVantageNewsResponse;
import com.pranjal.dtos.AlphaVantageDTOs.AlphaVantageResponse;
import com.pranjal.dtos.AlphaVantageDTOs.AlphaVantageStockHistoryResponse;
import com.pranjal.dtos.AlphaVantageDTOs.AlphaVantageStockOverviewResponse;
import com.pranjal.exception.ExternalApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@Slf4j
@RequiredArgsConstructor
public class StockClient {

    private final WebClient webClient;

    @Value("${alpha.vantage.api.key}")
    private String API_KEY;

    public AlphaVantageResponse getStockPrice(String symbol){
        try {
            return webClient.get().uri(uriBuilder -> uriBuilder
                            .queryParam("function", "GLOBAL_QUOTE")
                            .queryParam("symbol", symbol)
                            .queryParam("apikey", API_KEY)
                            .build())
                    .retrieve()
                    .bodyToMono(AlphaVantageResponse.class)
                    .block();
        } catch (ExternalApiException e) {
            log.error("Error fetching stock price: {}", e.getMessage());
            throw new ExternalApiException("Error while fetching stock price.");
        }
    }

    public AlphaVantageStockOverviewResponse getStockOverview(String symbol) {
        try {
            return webClient.get().uri(uriBuilder -> uriBuilder
                            .queryParam("function", "OVERVIEW")
                            .queryParam("symbol", symbol)
                            .queryParam("apikey", API_KEY)
                            .build())
                    .retrieve()
                    .bodyToMono(AlphaVantageStockOverviewResponse.class)
                    .block();
        }catch (ExternalApiException e){
            log.error("Error fetching stock overview: {}", e.getMessage());
            throw new ExternalApiException("Error while fetching stock overview.");
        }
    }

public AlphaVantageStockHistoryResponse getStockHistory(String symbol, String function) {
    log.info("Fetching stock history for symbol: {} and function: {}", symbol, function);

    try {
        return webClient.get()
                .uri(uriBuilder -> {
                    uriBuilder
                            .queryParam("symbol", symbol)
                            .queryParam("apikey", API_KEY)
                            .queryParam("function", function);
                    if ("TIME_SERIES_INTRADAY".equals(function)) {
                        uriBuilder.queryParam("interval", "5min");
                    }
                    return uriBuilder.build();
                })
                .retrieve()
                .bodyToMono(AlphaVantageStockHistoryResponse.class)
                .block();
    } catch (Exception e) {
        log.error("Error fetching stock history: {}", e.getMessage());
        throw new ExternalApiException("Error while fetching stock history.");
    }
}

    public AlphaVantageNewsResponse getNewsBySymbol(String symbols, int size){
        try {
            if(symbols.equals("none")){
                return webClient.get().uri(uriBuilder -> uriBuilder
                                .queryParam("function", "NEWS_SENTIMENT")
                                .queryParam("sort", "LATEST")
                                .queryParam("limit", size)
                                .queryParam("apikey", API_KEY)
                                .build())
                        .retrieve()
                        .bodyToMono(AlphaVantageNewsResponse.class)
                        .block();
            }
            return webClient.get().uri(uriBuilder -> uriBuilder
                            .queryParam("function", "NEWS_SENTIMENT")
                            .queryParam("tickers", "AAPL")
                            .queryParam("sort", "LATEST")
                            .queryParam("limit", size)
                            .queryParam("apikey", API_KEY)
                            .build())
                    .retrieve()
                    .bodyToMono(AlphaVantageNewsResponse.class)
                    .block();
        }catch (ExternalApiException e){
            log.error("Error fetching news: {}", e.getMessage());
            throw new ExternalApiException("Error while fetching news.");
        }
    }
}
