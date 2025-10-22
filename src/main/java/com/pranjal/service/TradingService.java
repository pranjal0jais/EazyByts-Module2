package com.pranjal.service;

import com.pranjal.dtos.*;
import com.pranjal.dtos.TradingDTOs.StockPurchaseResponse;
import com.pranjal.dtos.StocksDTOs.StockQuoteResponse;
import com.pranjal.dtos.TradingDTOs.StockSellResponse;
import com.pranjal.dtos.TradingDTOs.TradeRequest;
import com.pranjal.exception.*;
import com.pranjal.model.Holding;
import com.pranjal.model.Transaction;
import com.pranjal.model.User;
import com.pranjal.repository.HoldingRepository;
import com.pranjal.repository.TransactionRepository;
import com.pranjal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class TradingService {
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final HoldingRepository holdingRepository;
    private final StockService stockService;


    @Transactional
    @CacheEvict(value = "portfolio", key = "#userId")
    public StockPurchaseResponse buyStock(String userId, TradeRequest request){
        try {
            StockQuoteResponse quote;
            try {
                quote = stockService.getStockPrice(request.symbol());
            } catch (StockSymbolNotFoundException e) {
                throw new StockSymbolNotFoundException("Stock symbol not found: " + request.symbol());
            }

            User user = userRepository.findByUserId(userId)
                    .orElseThrow(() -> new UserNotFoundException("User not found with user id: " + userId));


            double totalPrice = quote.price() * request.quantity();

            if (user.getVirtualBalance() < totalPrice) {
                throw new InsufficientBalanceException("Insufficient balance for purchase: " + totalPrice);
            }

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
                    .createdAt(LocalDateTime.now())
                    .build();

            transaction = transactionRepository.save(transaction);

            Holding holding;
            if (!holdingRepository.existsByUser_UserIdAndStockSymbol(userId, request.symbol())) {
                holding = Holding.builder()
                        .user(user)
                        .holdingId(UUID.randomUUID().toString())
                        .stockSymbol(request.symbol())
                        .quantity(request.quantity())
                        .averagePrice(quote.price())
                        .build();
            } else {
                holding = holdingRepository.findByUser_UserIdAndStockSymbol(userId, request.symbol())
                        .orElseThrow(() -> new StockDoesNotExistException("Your Portfolio does not " +
                                "contain stock with symbol: " + request.symbol()));

                double oldPrice = holding.getAveragePrice() * holding.getQuantity();
                double newPrice = quote.price() * request.quantity();
                double newAveragePrice = (oldPrice + newPrice) / (holding.getQuantity() + request.quantity());

                holding.setQuantity(holding.getQuantity() + request.quantity());
                holding.setAveragePrice(newAveragePrice);
            }
            holdingRepository.save(holding);

            return StockPurchaseResponse.builder()
                    .transactionId(transaction.getTransactionId())
                    .type(TransactionType.BUY)
                    .stockSymbol(request.symbol())
                    .pricePerUnit(transaction.getPricePerUnit())
                    .totalAmount(transaction.getTotalAmount())
                    .quantity(transaction.getQuantity())
                    .createdAt(transaction.getCreatedAt().toString())
                    .build();
        } catch (StockSymbolNotFoundException | UserNotFoundException |
                 InsufficientBalanceException | StockDoesNotExistException e){
            throw e;
        } catch (Exception e){
            throw new TransactionFailedException("Transaction failed due to some reason");
        }
    }

    @CacheEvict(value = "portfolio", key = "#userId")
    @Transactional
    public StockSellResponse sellStock(String userId, TradeRequest request){
        try {
            StockQuoteResponse quote;
            try {
                quote = stockService.getStockPrice(request.symbol());
            } catch (StockSymbolNotFoundException e) {
                throw new StockSymbolNotFoundException("Stock symbol not found: " + request.symbol());
            }

            User user = userRepository.findByUserId(userId)
                    .orElseThrow(() -> new UserNotFoundException("User not found with user id: " + userId));

            Holding holding = holdingRepository.findByUser_UserIdAndStockSymbol(userId, request.symbol())
                    .orElseThrow(() -> new StockDoesNotExistException("Your Portfolio does not " +
                            "contain stock with symbol: " + request.symbol()));

            if (holding.getQuantity() < request.quantity()) {
                throw new InsufficientHoldingException("You don't have enough holding of this stock");
            }

            double boughtPrice = holding.getAveragePrice();
            double profitOrLossPerUnit = quote.price() - boughtPrice;
            double totalProfitOrLoss = profitOrLossPerUnit * request.quantity();
            double totalPrice = quote.price() * request.quantity();

            holding.setQuantity(holding.getQuantity() - request.quantity());

            user.setVirtualBalance(user.getVirtualBalance() + totalPrice);
            userRepository.save(user);

            if (holding.getQuantity() == 0) {
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
                    .createdAt(LocalDateTime.now())
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
        }catch (StockSymbolNotFoundException | UserNotFoundException |
                InsufficientBalanceException | StockDoesNotExistException |
                InsufficientHoldingException e){
            throw e;
        } catch (Exception e){
            throw new TransactionFailedException("Transaction failed due to some reason");
        }
    }
}
