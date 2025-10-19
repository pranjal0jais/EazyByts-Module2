package com.pranjal.controller;

import com.pranjal.dtos.TradingDTOs.StockSellResponse;
import com.pranjal.dtos.TradingDTOs.TradeRequest;
import com.pranjal.dtos.TradingDTOs.StockPurchaseResponse;
import com.pranjal.service.TradingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/trade")
@RequiredArgsConstructor
public class TradingController {

    private final TradingService tradingService;

    @PostMapping("/buy")
    public ResponseEntity<StockPurchaseResponse> buy(@Valid @RequestBody TradeRequest request,
                                                     @RequestParam String userId){
        return ResponseEntity.ok(tradingService.buyStock(userId,request));
    }

    @PostMapping("/sell")
    public ResponseEntity<StockSellResponse> sell(@Valid @RequestBody TradeRequest request,
                                                     @RequestParam String userId){
        return ResponseEntity.ok(tradingService.sellStock(userId,request));
    }
}
