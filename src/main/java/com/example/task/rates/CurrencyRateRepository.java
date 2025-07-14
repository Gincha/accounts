package com.example.task.rates;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CurrencyRateRepository extends JpaRepository<CurrencyRateEntity, Long> {

    @Query(value = "SELECT cr.rate "
            + " from currency_rates cr "
            + " where base=?1 and currency=?2 and max(cr.date)"
            , nativeQuery = true)
    Optional<BigDecimal> findLatestRateForBaseAndCurrency(String baseCurrency, String rateCurrency);

    @Query(value = "SELECT max(cr.date) "
            + " from currency_rates cr "
            , nativeQuery = true)
    Optional<LocalDate> findLatestCurrencyRateDate();

    List<CurrencyRateEntity> findAllByDate(LocalDate date);
}
