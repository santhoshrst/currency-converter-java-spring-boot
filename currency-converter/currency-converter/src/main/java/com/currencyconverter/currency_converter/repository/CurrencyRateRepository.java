package com.currencyconverter.currency_converter.repository;

import com.currencyconverter.currency_converter.model.CurrencyRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CurrencyRateRepository extends JpaRepository<CurrencyRate, Long> {
    Optional<CurrencyRate> findBySourceCurrencyAndTargetCurrency(String sourceCurrency, String targetCurrency);
}
