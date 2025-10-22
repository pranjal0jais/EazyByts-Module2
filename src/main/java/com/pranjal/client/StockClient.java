package com.pranjal.client;

import com.pranjal.dtos.AlphaVantageDTOs.AlphaVantageNewsResponse;
import com.pranjal.dtos.AlphaVantageDTOs.AlphaVantageResponse;
import com.pranjal.dtos.AlphaVantageDTOs.AlphaVantageStockHistoryResponse;
import com.pranjal.dtos.AlphaVantageDTOs.AlphaVantageStockOverviewResponse;
import com.pranjal.exception.ExternalApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@Slf4j
public class StockClient {

    @Autowired
    private WebClient webClient;

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
            throw new ExternalApiException("Error while fetching stock overview.");
        }
    }

    public AlphaVantageStockHistoryResponse getStockHistory(String symbol) {
        try {
            return webClient.get().uri(uriBuilder -> uriBuilder
                            .queryParam("function", "TIME_SERIES_DAILY")
                            .queryParam("symbol", symbol)
                            .queryParam("apikey", API_KEY)
                            .build())
                    .retrieve()
                    .bodyToMono(AlphaVantageStockHistoryResponse.class)
                    .block();
        }catch (ExternalApiException e){
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
            throw new ExternalApiException("Error while fetching news.");
        }
    }
}
