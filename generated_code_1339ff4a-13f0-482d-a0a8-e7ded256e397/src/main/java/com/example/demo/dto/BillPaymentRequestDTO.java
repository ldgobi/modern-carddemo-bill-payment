package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BillPaymentRequestDTO {
    
    @NotBlank(message = "Account ID cannot be empty")
    @Size(max = 11, message = "Account ID must not exceed 11 characters")
    private String accountId;
    
    @NotNull(message = "Payment confirmation is required")
    private Boolean confirmPayment;
}
