package com.example.demo.service;

import com.example.demo.dto.AccountBalanceDTO;
import com.example.demo.dto.BillPaymentRequestDTO;
import com.example.demo.dto.BillPaymentResponseDTO;
import com.example.demo.entity.Account;
import com.example.demo.entity.CardCrossReference;
import com.example.demo.entity.Transaction;
import com.example.demo.repository.AccountRepository;
import com.example.demo.repository.CardCrossReferenceRepository;
import com.example.demo.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BillPaymentService {
    
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final CardCrossReferenceRepository cardCrossReferenceRepository;
    
    public AccountBalanceDTO getAccountBalance(String accountId) {
        if (accountId == null || accountId.trim().isEmpty()) {
            throw new IllegalArgumentException("Account ID cannot be empty");
        }
        
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account ID NOT found..."));
        
        return new AccountBalanceDTO(account.getAccountId(), account.getCurrentBalance());
    }
    
    @Transactional
    public BillPaymentResponseDTO processBillPayment(BillPaymentRequestDTO request) {
        // Validate account ID
        if (request.getAccountId() == null || request.getAccountId().trim().isEmpty()) {
            throw new IllegalArgumentException("Account ID cannot be empty");
        }
        
        // Validate payment confirmation
        if (request.getConfirmPayment() == null || !request.getConfirmPayment()) {
            throw new IllegalArgumentException("Confirm to make a bill payment...");
        }
        
        // Retrieve account information
        Account account = accountRepository.findById(request.getAccountId())
                .orElseThrow(() -> new IllegalArgumentException("Account ID NOT found..."));
        
        // Check if balance is greater than zero
        if (account.getCurrentBalance().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("You have nothing to pay...");
        }
        
        // Retrieve card number from cross-reference
        CardCrossReference cardXref = cardCrossReferenceRepository.findByAccountId(request.getAccountId())
                .orElseThrow(() -> new IllegalArgumentException("Unable to lookup XREF AIX file..."));
        
        // Generate new transaction ID
        String newTransactionId = generateNewTransactionId();
        
        // Get current timestamp
        LocalDateTime currentTimestamp = LocalDateTime.now();
        
        // Store payment amount (current balance)
        BigDecimal paymentAmount = account.getCurrentBalance();
        
        // Create new transaction record
        Transaction transaction = new Transaction();
        transaction.setTransactionId(newTransactionId);
        transaction.setTransactionTypeCode("02");
        transaction.setTransactionCategoryCode(2);
        transaction.setTransactionSource("POS TERM");
        transaction.setTransactionDescription("BILL PAYMENT - ONLINE");
        transaction.setTransactionAmount(paymentAmount);
        transaction.setCardNumber(cardXref.getCardNumber());
        transaction.setMerchantId(999999999L);
        transaction.setMerchantName("BILL PAYMENT");
        transaction.setMerchantCity("N/A");
        transaction.setMerchantZip("N/A");
        transaction.setOriginTimestamp(currentTimestamp);
        transaction.setProcessTimestamp(currentTimestamp);
        transaction.setAccountId(account.getAccountId());
        
        // Save transaction
        transactionRepository.save(transaction);
        
        // Update account balance
        account.processPayment(paymentAmount);
        accountRepository.save(account);
        
        // Return success response
        String successMessage = String.format("Payment successful. Your Transaction ID is %s.", newTransactionId);
        return new BillPaymentResponseDTO(
                newTransactionId,
                successMessage,
                account.getAccountId(),
                paymentAmount,
                account.getCurrentBalance()
        );
    }
    
    private String generateNewTransactionId() {
        return transactionRepository.findTopByOrderByTransactionIdDesc()
                .map(lastTransaction -> {
                    try {
                        long lastId = Long.parseLong(lastTransaction.getTransactionId());
                        long newId = lastId + 1;
                        return String.format("%016d", newId);
                    } catch (NumberFormatException e) {
                        throw new IllegalStateException("Unable to generate new transaction ID");
                    }
                })
                .orElse("0000000000000001");
    }
}
