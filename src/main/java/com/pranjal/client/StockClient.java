package com.pranjal.client;

import com.pranjal.dtos.AlphaVantageResponse;
import com.pranjal.dtos.AlphaVantageStockHistory;
import com.pranjal.dtos.AlphaVantageStockOverviewResponse;
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
        return webClient.get().uri(uriBuilder -> uriBuilder
                        .queryParam("function", "GLOBAL_QUOTE")
                        .queryParam("symbol", symbol)
                        .queryParam("apikey", API_KEY)
                        .build())
                .retrieve()
                .bodyToMono(AlphaVantageResponse.class)
                .block();
    }

    public AlphaVantageStockOverviewResponse getStockOverview(String symbol) {

        return webClient.get().uri(uriBuilder -> uriBuilder
                    .queryParam("function", "OVERVIEW")
                    .queryParam("symbol", symbol)
                    .queryParam("apikey", API_KEY)
                    .build())
                .retrieve()
                .bodyToMono(AlphaVantageStockOverviewResponse.class)
                .block();
    }

    public AlphaVantageStockHistory getStockHistory(String symbol) {
        return webClient.get().uri(uriBuilder -> uriBuilder
                        .queryParam("function", "TIME_SERIES_DAILY")
                        .queryParam("symbol", symbol)
                        .queryParam("apikey", API_KEY)
                        .build())
                .retrieve()
                .bodyToMono(AlphaVantageStockHistory.class)
                .block();
    }
}
