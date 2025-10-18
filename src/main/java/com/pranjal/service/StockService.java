package com.pranjal.service;

import com.pranjal.client.StockClient;
import com.pranjal.dtos.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StockService {
    private final StockClient stockClient;

    public List<DailyStockHistory> getDailyStockHistory(String symbol,
                                                                              int days) {
        AlphaVantageStockHistory response = stockClient.getStockHistory(symbol);
        return response.timeSeries()
                .entrySet()
                .stream()
                .limit(days)
                .map(entry->{
                    var date = entry.getKey();
                    var daily = entry.getValue();
                    return new DailyStockHistory(
                            date,
                            Double.parseDouble(daily.open()),
                            Double.parseDouble(daily.high()),
                            Double.parseDouble(daily.low()),
                            Double.parseDouble(daily.close()),
                            Long.parseLong(daily.volume())
                    );
                }).toList();
    }

    public StockOverviewResponse getStockOverview(String symbol){
        AlphaVantageStockOverviewResponse response = stockClient.getStockOverview(symbol);
        return new StockOverviewResponse(
                response.symbol(),
                response.name(),
                response.description(),
                response.exchange(),
                response.country(),
                response.officialSite(),
                response.sector()
        );
    }

    public StockQuoteResponse getStockPrice(String symbol){
        AlphaVantageResponse clientResponse = stockClient.getStockPrice(symbol);
        return StockQuoteResponse.builder()
                .symbol(clientResponse.globalQuote().symbol())
                .price(Double.parseDouble(clientResponse.globalQuote().price()))
                .build();
    }
}
