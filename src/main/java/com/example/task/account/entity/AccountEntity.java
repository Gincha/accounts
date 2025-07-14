package com.example.task.account.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "accounts")
public class AccountEntity {

    @Id
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    private ClientEntity clientId;
    private String currency;
    private BigDecimal balance = BigDecimal.ZERO;
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    private List<TransactionEntity> transactions = new ArrayList<>();

    public void add(TransactionEntity transaction) {
        transaction.setAccount(this);
        this.transactions.add(transaction);
    }
}
