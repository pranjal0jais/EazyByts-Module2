package com.pranjal.controller;

import com.pranjal.dtos.StockSellResponse;
import com.pranjal.dtos.TradeRequest;
import com.pranjal.dtos.StockPurchaseResponse;
import com.pranjal.service.TradingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/trade")
@RequiredArgsConstructor
public class TradingController {

    private final TradingService tradingService;

    @PostMapping("/buy")
    public ResponseEntity<StockPurchaseResponse> buy(@RequestBody TradeRequest request,
                                                     @RequestParam String userId){
        return ResponseEntity.ok(tradingService.buyStock(userId,request));
    }

    @PostMapping("/sell")
    public ResponseEntity<StockSellResponse> sell(@RequestBody TradeRequest request,
                                                     @RequestParam String userId){
        return ResponseEntity.ok(tradingService.sellStock(userId,request));
    }
}
