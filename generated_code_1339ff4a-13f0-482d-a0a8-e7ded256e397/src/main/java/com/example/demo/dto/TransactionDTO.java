package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDTO {
    
    private String transactionId;
    private String transactionTypeCode;
    private Integer transactionCategoryCode;
    private String transactionSource;
    private String transactionDescription;
    private BigDecimal transactionAmount;
    private String cardNumber;
    private Long merchantId;
    private String merchantName;
    private String merchantCity;
    private String merchantZip;
    private LocalDateTime originTimestamp;
    private LocalDateTime processTimestamp;
    private String accountId;
}
