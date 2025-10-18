package com.pranjal.controller;

import com.pranjal.dtos.PortfolioResponse;
import com.pranjal.service.PortfolioService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users/portfolio")
@RequiredArgsConstructor
public class PortfolioController {
    private final PortfolioService portfolioService;

    @GetMapping
    public PortfolioResponse getPortfolioByUser(@RequestParam("userId") String userId){
        return portfolioService.getPortfolioByUser(userId);
    }
}
