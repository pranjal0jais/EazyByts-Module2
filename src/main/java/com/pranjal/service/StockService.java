package com.pranjal.service;

import com.pranjal.client.StockClient;
import com.pranjal.dtos.AlphaVantageDTOs.AlphaVantageResponse;
import com.pranjal.dtos.AlphaVantageDTOs.AlphaVantageStockHistoryResponse;
import com.pranjal.dtos.AlphaVantageDTOs.AlphaVantageStockOverviewResponse;
import com.pranjal.dtos.StocksDTOs.DailyStockHistory;
import com.pranjal.dtos.StocksDTOs.StockOverviewResponse;
import com.pranjal.dtos.StocksDTOs.StockQuoteResponse;
import com.pranjal.exception.StockSymbolNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StockService {
    private final StockClient stockClient;

    @Transactional(readOnly = true)
    public List<DailyStockHistory> getDailyStockHistory(String symbol,
                                                        int days) {
        try {
            AlphaVantageStockHistoryResponse response = stockClient.getStockHistory(symbol);
            return response.timeSeries()
                    .entrySet()
                    .stream()
                    .limit(days)
                    .map(entry -> {
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
        } catch (Exception e){
            throw new StockSymbolNotFoundException("Stock symbol not found: " + symbol);
        }
    }

    @Transactional(readOnly = true)
    public StockOverviewResponse getStockOverview(String symbol){
        try {
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
        } catch (Exception e){
            throw new StockSymbolNotFoundException("Stock symbol not found: " + symbol);
        }
    }

    @Transactional(readOnly = true)
    public StockQuoteResponse getStockPrice(String symbol){
        try {
            AlphaVantageResponse clientResponse = stockClient.getStockPrice(symbol);
            return StockQuoteResponse.builder()
                    .symbol(clientResponse.globalQuote().symbol())
                    .price(Double.parseDouble(clientResponse.globalQuote().price()))
                    .build();
        }catch (Exception e){
            throw new StockSymbolNotFoundException("Stock symbol not found: " + symbol);
        }
    }
}
