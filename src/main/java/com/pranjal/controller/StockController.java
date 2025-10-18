package com.pranjal.controller;

import com.pranjal.dtos.DailyStockHistory;
import com.pranjal.dtos.StockOverviewDTO;
import com.pranjal.dtos.StockQuoteDTO;
import com.pranjal.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/stocks")
@RequiredArgsConstructor
public class StockController {
    private final StockService stockService;

    @GetMapping("/quote")
    public ResponseEntity<StockQuoteDTO> getQuoteBySymbol(@RequestParam("symbol") String symbol){
        return ResponseEntity.ok(stockService.getStockPrice(symbol));
    }

    @GetMapping("/overview")
    public ResponseEntity<StockOverviewDTO> getOverviewBySymbol(@RequestParam("symbol") String symbol){
        return ResponseEntity.ok(stockService.getStockOverview(symbol));
    }

    @GetMapping("/history")
    public ResponseEntity<List<DailyStockHistory>> getHistoryBySymbol(
            @RequestParam("symbol") String symbol,
            @RequestParam(value = "days", defaultValue = "30") int days){
        return ResponseEntity.ok(stockService.getDailyStockHistory(symbol, 30));
    }
}
