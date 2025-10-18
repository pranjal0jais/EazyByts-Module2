package com.pranjal.repository;

import com.pranjal.model.Holding;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HoldingRepository extends JpaRepository<Holding, Long> {
    boolean existsByUser_UserIdAndStockSymbol(String userId, String stockSymbol);

    Optional<Holding> findByUser_UserIdAndStockSymbol(String userId, String stockSymbol);
}
