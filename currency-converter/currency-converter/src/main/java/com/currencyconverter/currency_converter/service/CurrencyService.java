package com.currencyconverter.currency_converter.service;

import com.currencyconverter.currency_converter.model.CurrencyRate;
import com.currencyconverter.currency_converter.repository.CurrencyRateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CurrencyService {
    @Autowired
    private CurrencyRateRepository currencyRateRepository;
    private final RestTemplate restTemplate = new RestTemplate(); // or WebClient if needed
    private final String API_KEY = "1SMxQumaAcC996SUXGsnlBPW44t2RR82"; // put in application.properties later

    public double convert(String source, String target, int amount) {
        Optional<CurrencyRate> optionalRate = currencyRateRepository.findBySourceCurrencyAndTargetCurrency(source, target);
        CurrencyRate currencyRate;

        if (optionalRate.isPresent() && optionalRate.get().getLastUpdated().isAfter(LocalDateTime.now().minus(1, ChronoUnit.HOURS))) {
            currencyRate = optionalRate.get();
        } else if(optionalRate.isPresent()) {
            currencyRate = fetchFromCurrencyBeaconAndUpdate(source, target);
        }else{
            currencyRate = fetchFromCurrencyBeaconAndSave(source, target);
        }

        return amount * currencyRate.getRate();
    }

    private CurrencyRate fetchFromCurrencyBeaconAndSave(String source, String target) {
        String url = String.format(
                "https://api.currencybeacon.com/v1/convert?from=%s&to=%s&amount=1&api_key=%s",
                source, target, API_KEY
        );
        // Call API
        var response = restTemplate.getForObject(url, CurrencyBeaconResponse.class);
        CurrencyRate rate = new CurrencyRate();
        rate.setSourceCurrency(source);
        rate.setTargetCurrency(target);
        rate.setRate(response.getValue());
        rate.setLastUpdated(LocalDateTime.now());
        currencyRateRepository.save(rate);

        return rate;
    }

    private CurrencyRate fetchFromCurrencyBeaconAndUpdate(String source, String target) {
        String url = String.format(
                "https://api.currencybeacon.com/v1/convert?from=%s&to=%s&amount=1&api_key=%s",
                source, target, API_KEY
        );
        // Call API
        var response = restTemplate.getForObject(url, CurrencyBeaconResponse.class);
        CurrencyRate rate = currencyRateRepository.findBySourceCurrencyAndTargetCurrency(source,target).get();
        rate.setSourceCurrency(source);
        rate.setTargetCurrency(target);
        rate.setRate(response.getValue());
        rate.setLastUpdated(LocalDateTime.now());
        currencyRateRepository.save(rate);
        return rate;
    }

    static class CurrencyBeaconResponse {
        private double value;
        public double getValue() { return value; }
        public void setValue(double value) { this.value = value; }
    }
}
