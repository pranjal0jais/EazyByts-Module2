package com.pranjal.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="holdings")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Holding {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Column(name="holding_id", unique = true, nullable = false)
    private String holdingId;

    @Column(name="stock_symbol", nullable = false, unique = true)
    private String stockSymbol;

    @Column(name="quantity", nullable = false)
    private int quantity;

    @Column(name="average_price", nullable = false)
    private double averagePrice;

    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;
}
