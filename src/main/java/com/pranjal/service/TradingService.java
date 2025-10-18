package com.pranjal.service;

import com.pranjal.client.StockClient;
import com.pranjal.dtos.*;
import com.pranjal.model.Holding;
import com.pranjal.model.Transaction;
import com.pranjal.model.User;
import com.pranjal.repository.HoldingRepository;
import com.pranjal.repository.TransactionRepository;
import com.pranjal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class TradingService {
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final StockClient stockClient;
    private final HoldingRepository holdingRepository;
    private final StockService stockService;


    @Transactional
    public StockPurchaseResponse buyStock(String userId, TradeRequest request){
        StockQuoteResponse quote;
        try {
            quote = stockService.getStockPrice(request.symbol());
            log.info("Quote: {}", quote);
        } catch (Exception e) {
            log.error("Error while fetching stock price", e);
            throw new RuntimeException("Error while fetching stock price");
        }

        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));


        double totalPrice = quote.price() * request.quantity();

        log.info("Total Price: {}", totalPrice);

        if(user.getVirtualBalance() < totalPrice){
            log.error("Insufficient balance");
            throw new RuntimeException("Insufficient balance");
        }

        log.info("Virtual Balance: {}", user.getVirtualBalance());

        user.setVirtualBalance(user.getVirtualBalance() - totalPrice);
        userRepository.save(user);

        Transaction transaction = Transaction.builder()
                .transactionId(UUID.randomUUID().toString())
                .stockSymbol(request.symbol())
                .type(TransactionType.BUY)
                .pricePerUnit(quote.price())
                .quantity(request.quantity())
                .totalAmount(totalPrice)
                .user(user)
                .build();

        transaction = transactionRepository.save(transaction);
        log.info("Transaction saved: {}", transaction);

        Holding holding;
        if(!holdingRepository.existsByUser_UserIdAndStockSymbol(userId, request.symbol())){
               holding = Holding.builder()
                       .user(user)
                       .holdingId(UUID.randomUUID().toString())
                       .stockSymbol(request.symbol())
                       .quantity(request.quantity())
                       .averagePrice(quote.price())
                       .build();
        } else {
            holding = holdingRepository.findByUser_UserIdAndStockSymbol(userId, request.symbol())
                    .orElseThrow(() -> new RuntimeException("Error while fetching holding"));

            double oldPrice = holding.getAveragePrice() * holding.getQuantity();
            double newPrice = quote.price() * request.quantity();
            double newAveragePrice = (oldPrice + newPrice) / (holding.getQuantity() + request.quantity());

            holding.setQuantity(holding.getQuantity() + request.quantity());
            holding.setAveragePrice(newAveragePrice);
            log.info("New Average Price: {}", newAveragePrice);
        }
        holdingRepository.save(holding);

        log.info("Holding saved: {}", holding);

        return StockPurchaseResponse.builder()
                .transactionId(transaction.getTransactionId())
                .type(TransactionType.BUY)
                .stockSymbol(request.symbol())
                .pricePerUnit(transaction.getPricePerUnit())
                .totalAmount(transaction.getTotalAmount())
                .quantity(transaction.getQuantity())
                .createdAt(transaction.getCreatedAt().toString())
                .build();
    }

    @Transactional
    public StockSellResponse sellStock(String userId, TradeRequest request){
        StockQuoteResponse quote;
        try{
            quote = stockService.getStockPrice(request.symbol());
            log.info("Quote: {}", quote);
        } catch(Exception e){
            log.error("Error while fetching stock price", e);
            throw new RuntimeException("Error while fetching stock price");
        }

        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Holding holding = holdingRepository.findByUser_UserIdAndStockSymbol(userId, request.symbol())
                .orElseThrow(() -> new RuntimeException("Error while fetching holding"));

        if(holding.getQuantity() < request.quantity()){
            throw new RuntimeException("Insufficient holding");
        }

        double boughtPrice = holding.getAveragePrice();
        double profitOrLossPerUnit = quote.price() - boughtPrice;
        double totalProfitOrLoss = profitOrLossPerUnit * request.quantity();
        double totalPrice = quote.price() * request.quantity();

        holding.setQuantity(holding.getQuantity() - request.quantity());

        user.setVirtualBalance(user.getVirtualBalance() + totalPrice);
        userRepository.save(user);

        if(holding.getQuantity() == 0){
            holdingRepository.delete(holding);
        } else {
            holdingRepository.save(holding);
        }

        Transaction transaction = Transaction.builder()
                .transactionId(UUID.randomUUID().toString())
                .stockSymbol(request.symbol())
                .type(TransactionType.SELL)
                .pricePerUnit(quote.price())
                .quantity(request.quantity())
                .totalAmount(totalPrice)
                .user(user)
                .build();

        transaction = transactionRepository.save(transaction);

        return StockSellResponse.builder()
                .transactionId(transaction.getTransactionId())
                .type(transaction.getType())
                .stockSymbol(transaction.getStockSymbol())
                .pricePerUnit(transaction.getPricePerUnit())
                .profit(totalProfitOrLoss > 0)
                .profitOrLoss(totalProfitOrLoss)
                .totalAmount(transaction.getTotalAmount())
                .quantity(transaction.getQuantity())
                .createdAt(transaction.getCreatedAt().toString())
                .build();
    }
}
