package com.pranjal.controller;

import com.pranjal.dtos.StocksDTOs.DailyStockHistory;
import com.pranjal.dtos.StocksDTOs.StockNewsResponse;
import com.pranjal.dtos.StocksDTOs.StockOverviewResponse;
import com.pranjal.dtos.StocksDTOs.StockQuoteResponse;
import com.pranjal.service.StockService;
import com.pranjal.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
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
    private final UserService userService;

    @GetMapping("/quote")
    public ResponseEntity<StockQuoteResponse> getQuoteBySymbol(@RequestParam("symbol") String symbol){
        return ResponseEntity.ok(stockService.getStockPrice(symbol));
    }

    @GetMapping("/overview")
    public ResponseEntity<StockOverviewResponse> getOverviewBySymbol(@RequestParam("symbol") String symbol){
        return ResponseEntity.ok(stockService.getStockOverview(symbol));
    }

    @GetMapping("/history")
    public ResponseEntity<List<DailyStockHistory>> getHistoryBySymbol(
            @RequestParam("symbol") String symbol,
            @RequestParam(value = "days", defaultValue = "30") int days,
            @RequestParam(value = "function") String function){
        return ResponseEntity.ok(stockService.getDailyStockHistory(symbol, days, function));
    }

    @GetMapping("/news")
    public ResponseEntity<List<StockNewsResponse>> getNewsBySymbols(@AuthenticationPrincipal Jwt jwt,
                                                                    @RequestParam(value = "size",
                                                                            defaultValue = "10") int size){
        String email = jwt.getSubject();
        return ResponseEntity.ok(stockService.getNewsByTickers(userService.getUserIdByEmail(email), size));
    }
}
