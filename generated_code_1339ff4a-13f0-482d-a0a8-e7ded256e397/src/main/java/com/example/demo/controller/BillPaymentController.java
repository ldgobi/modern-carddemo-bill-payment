package com.example.demo.controller;

import com.example.demo.dto.AccountBalanceDTO;
import com.example.demo.dto.BillPaymentRequestDTO;
import com.example.demo.dto.BillPaymentResponseDTO;
import com.example.demo.service.BillPaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bill-payment")
@RequiredArgsConstructor
public class BillPaymentController {
    
    private final BillPaymentService billPaymentService;
    
    @GetMapping("/account/{accountId}/balance")
    public ResponseEntity<AccountBalanceDTO> getAccountBalance(@PathVariable String accountId) {
        try {
            AccountBalanceDTO balance = billPaymentService.getAccountBalance(accountId);
            return ResponseEntity.ok(balance);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    
    @PostMapping("/process")
    public ResponseEntity<BillPaymentResponseDTO> processBillPayment(
            @Valid @RequestBody BillPaymentRequestDTO request) {
        try {
            BillPaymentResponseDTO response = billPaymentService.processBillPayment(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}
