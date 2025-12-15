package com.travel_agent.models.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "transaction_info")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private Integer transactionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(name = "payment_method", nullable = false)
    private String paymentMethod;

    @Column(name = "content", length = 500)
    private String content;

    @Column(name = "transaction_date", nullable = false)
    private LocalDate dates;
}
