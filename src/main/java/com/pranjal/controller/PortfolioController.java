package com.pranjal.controller;

import com.pranjal.dtos.PortfolioDTOs.PortfolioResponse;
import com.pranjal.service.PortfolioService;
import com.pranjal.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequestMapping("/api/v1/users/portfolio")
@RequiredArgsConstructor
public class PortfolioController {
    private final PortfolioService portfolioService;
    private final UserService userService;

    @GetMapping
    public PortfolioResponse getPortfolioByUser(@AuthenticationPrincipal Jwt jwt){
        String email = jwt.getSubject();
        return portfolioService.getPortfolioByUser(userService.getUserIdByEmail(email));
    }
}
