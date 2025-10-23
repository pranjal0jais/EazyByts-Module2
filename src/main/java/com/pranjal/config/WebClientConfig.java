package com.pranjal.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${alpha.vantage.base.url}")
    private String baseUrl;

    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        int sizeInBytes = 16 * 1024 * 1024;
        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(configurer -> configurer
                        .defaultCodecs()
                        .maxInMemorySize(sizeInBytes))
                .build();
        return builder
                .baseUrl(baseUrl)
                .exchangeStrategies(strategies)
                .build();
    }
}
