package com.pranjal.model;

import com.pranjal.dtos.TradingDTOs.TransactionType;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name="transactions")
@Getter
@Setter
@Builder
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Column(name="transaction_id", unique = true, nullable = false)
    private String transactionId;

    @Column(name="stock_symbol", nullable = false)
    private String stockSymbol;

    @Column(name="type", nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionType type;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    private User user;

    @Column(name="price_per_unit", nullable = false)
    private double pricePerUnit;

    @Column(name="quantity", nullable = false)
    private int quantity;

    @Column(name="total_amount", nullable = false)
    private double totalAmount;

    @CreationTimestamp
    @Column(name="created_at")
    private LocalDateTime createdAt;

}
