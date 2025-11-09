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
@Table(name = "transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    
    @Id
    @Column(name = "transaction_id", length = 16, nullable = false)
    private String transactionId;
    
    @Column(name = "transaction_type_code", length = 2, nullable = false)
    private String transactionTypeCode;
    
    @Column(name = "transaction_category_code", nullable = false)
    private Integer transactionCategoryCode;
    
    @Column(name = "transaction_source", length = 10, nullable = false)
    private String transactionSource;
    
    @Column(name = "transaction_description", length = 100, nullable = false)
    private String transactionDescription;
    
    @Column(name = "transaction_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal transactionAmount;
    
    @Column(name = "card_number", length = 16, nullable = false)
    private String cardNumber;
    
    @Column(name = "merchant_id", nullable = false)
    private Long merchantId;
    
    @Column(name = "merchant_name", length = 50, nullable = false)
    private String merchantName;
    
    @Column(name = "merchant_city", length = 50, nullable = false)
    private String merchantCity;
    
    @Column(name = "merchant_zip", length = 10, nullable = false)
    private String merchantZip;
    
    @Column(name = "origin_timestamp", nullable = false)
    private LocalDateTime originTimestamp;
    
    @Column(name = "process_timestamp", nullable = false)
    private LocalDateTime processTimestamp;
    
    @Column(name = "account_id", length = 11, nullable = false)
    private String accountId;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    public Transaction(String transactionId, String transactionTypeCode, Integer transactionCategoryCode, 
                      String transactionSource, String transactionDescription, BigDecimal transactionAmount, 
                      String cardNumber, Long merchantId, String merchantName, String merchantCity, 
                      String merchantZip, LocalDateTime originTimestamp, LocalDateTime processTimestamp, 
                      String accountId) {
        this.transactionId = transactionId;
        this.transactionTypeCode = transactionTypeCode;
        this.transactionCategoryCode = transactionCategoryCode;
        this.transactionSource = transactionSource;
        this.transactionDescription = transactionDescription;
        this.transactionAmount = transactionAmount;
        this.cardNumber = cardNumber;
        this.merchantId = merchantId;
        this.merchantName = merchantName;
        this.merchantCity = merchantCity;
        this.merchantZip = merchantZip;
        this.originTimestamp = originTimestamp;
        this.processTimestamp = processTimestamp;
        this.accountId = accountId;
    }
}
