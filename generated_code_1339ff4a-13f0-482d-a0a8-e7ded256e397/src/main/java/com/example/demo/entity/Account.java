package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "accounts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Account {
    
    @Id
    @Column(name = "account_id", length = 11, nullable = false)
    private String accountId;
    
    @Column(name = "current_balance", nullable = false, precision = 19, scale = 2)
    private BigDecimal currentBalance = BigDecimal.ZERO;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    public Account(String accountId, BigDecimal currentBalance) {
        this.accountId = accountId;
        this.currentBalance = currentBalance != null ? currentBalance : BigDecimal.ZERO;
    }
    
    public void processPayment(BigDecimal paymentAmount) {
        if (paymentAmount != null) {
            this.currentBalance = this.currentBalance.subtract(paymentAmount);
        }
    }
}
