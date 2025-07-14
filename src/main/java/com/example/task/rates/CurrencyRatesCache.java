package com.example.task.rates;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CurrencyRatesCache {

    private final CurrencyRateRepository currencyRateRepository;
    private final Map<String, Map<String, BigDecimal>> currencyRatesCache;

    public CurrencyRatesCache(CurrencyRateRepository currencyRateRepository) {
        this.currencyRateRepository = currencyRateRepository;
        this.currencyRatesCache = initCache();
    }

    public BigDecimal currencyRate(String base, String currency) {
        return currencyRatesCache.get(base).get(currency);
    }

    private Map<String, Map<String, BigDecimal>> initCache() {
        return fetchCurrencyRates().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        e -> e.getValue().stream()
                                .collect(Collectors.toMap(CurrencyRateEntity::getCurrency, CurrencyRateEntity::getRate))));
    }

    private Map<String, List<CurrencyRateEntity>> fetchCurrencyRates() {
        return currencyRateRepository.findLatestCurrencyRateDate()
                .map(latestDate -> currencyRateRepository.findAllByDate(latestDate)
                        .stream()
                        .collect(Collectors.groupingBy(CurrencyRateEntity::getBase)))
                .orElseThrow(() -> new IllegalStateException("Unable to fetch currency rate cache"));
    }

}
