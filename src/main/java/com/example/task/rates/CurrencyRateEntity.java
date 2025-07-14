package com.example.task.rates;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(name = "currency_rates")
public class CurrencyRateEntity {

    @Id
    private Long id;
    private String base;
    private String currency;
    private LocalDate date;
    private BigDecimal rate;
}
