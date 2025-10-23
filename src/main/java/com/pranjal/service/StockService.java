package com.pranjal.service;

import com.pranjal.client.StockClient;
import com.pranjal.dtos.AlphaVantageDTOs.AlphaVantageNewsResponse;
import com.pranjal.dtos.AlphaVantageDTOs.AlphaVantageResponse;
import com.pranjal.dtos.AlphaVantageDTOs.AlphaVantageStockHistoryResponse;
import com.pranjal.dtos.AlphaVantageDTOs.AlphaVantageStockOverviewResponse;
import com.pranjal.dtos.StocksDTOs.DailyStockHistory;
import com.pranjal.dtos.StocksDTOs.StockNewsResponse;
import com.pranjal.dtos.StocksDTOs.StockOverviewResponse;
import com.pranjal.dtos.StocksDTOs.StockQuoteResponse;
import com.pranjal.exception.StockSymbolNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockService {
    private final StockClient stockClient;
    private final UserService userService;

    @Cacheable(value = "stockHistory", key = "#symbol + '-' + #function", unless = "#result == " +
            "null")
    @Transactional(readOnly = true)
    public List<DailyStockHistory> getDailyStockHistory(String symbol,
                                                        int days, String function) {
        try {
            AlphaVantageStockHistoryResponse response = stockClient.getStockHistory(symbol, function);
            List<DailyStockHistory> dailyStockHistories = response.timeSeries()
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
            return dailyStockHistories.reversed();
        } catch (Exception e){
            throw new StockSymbolNotFoundException("Stock symbol not found: " + symbol);
        }
    }

    @Cacheable(value = "stockOverview", key = "#symbol", unless = "#result == null")
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

    @Cacheable(value = "stockQuotes", key = "#symbol", unless = "#result == null")
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

    @Cacheable(value = "stockNews", key = "#userId", unless = "#result == null")
    @Transactional(readOnly = true)
    public List<StockNewsResponse> getNewsByTickers(String userId,int size){
        String tickers = "";
        try{
            tickers = userService.getAllSymbol(userId);
            AlphaVantageNewsResponse response = stockClient.getNewsBySymbol(tickers, size);
            return response.feed().stream()
                    .map(feed -> new StockNewsResponse(
                            feed.title(),
                            feed.url(),
                            feed.publishedAt(),
                            feed.summary(),
                            feed.image(),
                            feed.source(),
                            feed.sentiment(),
                            feed.tickerSentiment().stream()
                                    .map(tickerSentiment -> new StockNewsResponse.TickerSentiment(
                                            tickerSentiment.ticker(),
                                            tickerSentiment.sentiment()
                                    )).toList()
                    ))
                    .toList();
        }catch (Exception e){
            throw new StockSymbolNotFoundException("Stock symbol not found: " + tickers);
        }
    }
}
