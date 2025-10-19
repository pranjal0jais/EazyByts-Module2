package com.pranjal.controller;

import com.pranjal.dtos.TradingDTOs.StockSellResponse;
import com.pranjal.dtos.TradingDTOs.TradeRequest;
import com.pranjal.dtos.TradingDTOs.StockPurchaseResponse;
import com.pranjal.service.TradingService;
import com.pranjal.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/trade")
@RequiredArgsConstructor
public class TradingController {

    private final TradingService tradingService;
    private final UserService userService;

    @PostMapping("/buy")
    public ResponseEntity<StockPurchaseResponse> buy(@Valid @RequestBody TradeRequest request,
                                                     @AuthenticationPrincipal Jwt jwt){
        String email = jwt.getSubject();
        return ResponseEntity.ok(tradingService.buyStock(userService.getUserIdByEmail(email),request));
    }

    @PostMapping("/sell")
    public ResponseEntity<StockSellResponse> sell(@Valid @RequestBody TradeRequest request,
                                                  @AuthenticationPrincipal Jwt jwt){
        String email = jwt.getSubject();
        return ResponseEntity.ok(tradingService.sellStock(userService.getUserIdByEmail(email),request));
    }
}
