package com.example.task.account.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "transactions")
public class TransactionEntity {

    @Id
    private UUID id = UUID.randomUUID();
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private AccountEntity account;
    private BigDecimal amount;
    private LocalDate date;

    public TransactionEntity(AccountEntity account, BigDecimal amount) {
        this.account = account;
        this.amount = amount;
        this.date = LocalDate.now();
    }

    public TransactionEntity() {
    }
}
