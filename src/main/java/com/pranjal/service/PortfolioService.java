package com.pranjal.service;

import com.pranjal.dtos.PortfolioResponse;
import com.pranjal.dtos.StocksDTOs.StockQuoteResponse;
import com.pranjal.model.Holding;
import com.pranjal.repository.HoldingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PortfolioService {
    private final HoldingRepository holdingRepository;
    private final StockService stockService;

    @Transactional(readOnly = true)
    public PortfolioResponse getPortfolioByUser(String userId){
        List<Holding> holding = holdingRepository.findAllByUser_UserIdOrderByStockSymbolAsc(userId);

        int totalStocks = holding.size();
        double totalInvestedValue = 0.0;
        double totalProfitLoss = 0.0;
        double totalCurrentValue = 0.0;
        List<PortfolioResponse.StockSummary> stockSummary = new ArrayList<>();

        for(Holding hol : holding){

            StockQuoteResponse quote = stockService.getStockPrice(hol.getStockSymbol());
            double currentStockValue = quote.price()*hol.getQuantity();
            double investedStockValue = hol.getAveragePrice()*hol.getQuantity();
            double stockProfitLoss = (quote.price() - hol.getAveragePrice())*hol.getQuantity();

            totalInvestedValue += investedStockValue;
            totalCurrentValue += currentStockValue;
            totalProfitLoss += stockProfitLoss;

            stockSummary.add(PortfolioResponse.StockSummary.builder()
                    .symbol(hol.getStockSymbol())
                    .currentValue(currentStockValue)
                    .investedValue(investedStockValue)
                    .profitLoss(stockProfitLoss)
                    .quantity(hol.getQuantity())
                    .build());
        }
        return PortfolioResponse.builder()
                .totalStocks(totalStocks)
                .totalInvestedValue(totalInvestedValue)
                .totalProfitLoss(totalProfitLoss)
                .totalCurrentValue(totalCurrentValue)
                .stockSummary(stockSummary)
                .build();
    }
}
