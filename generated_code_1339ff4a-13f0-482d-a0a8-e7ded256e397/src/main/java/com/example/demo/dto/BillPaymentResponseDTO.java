package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BillPaymentResponseDTO {
    
    private String transactionId;
    private String message;
    private String accountId;
    private BigDecimal paymentAmount;
    private BigDecimal newBalance;
}
