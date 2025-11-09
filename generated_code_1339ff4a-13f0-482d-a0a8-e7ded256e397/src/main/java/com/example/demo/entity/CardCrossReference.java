package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "card_cross_reference", indexes = {
    @Index(name = "idx_account_id", columnList = "account_id", unique = true)
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardCrossReference {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "account_id", nullable = false, length = 11, unique = true)
    private String accountId;
    
    @Column(name = "card_number", nullable = false, length = 16)
    private String cardNumber;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    public CardCrossReference(String accountId, String cardNumber) {
        this.accountId = accountId;
        this.cardNumber = cardNumber;
    }
}
