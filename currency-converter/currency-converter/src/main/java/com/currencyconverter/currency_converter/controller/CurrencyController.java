package com.currencyconverter.currency_converter.controller;

import com.currencyconverter.currency_converter.service.CurrencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import java.net.URI;

import java.util.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

@Controller
@RequiredArgsConstructor
public class CurrencyController {
    @Autowired
    private CurrencyService currencyService;

    @GetMapping("/")
    public String showForm(Model model) {
        model.addAttribute("currencyCodes", getCurrencyCodes());
        return "convert";
    }

    @PostMapping("/convert")
    public String convert(@RequestParam String sourceCurrency,
                          @RequestParam String targetCurrency,
                          @RequestParam int amount,
                          Model model) {
        double result = currencyService.convert(sourceCurrency, targetCurrency, amount);
        model.addAttribute("result", result);
        model.addAttribute("targetCurrency", targetCurrency);
        model.addAttribute("currencyCodes", getCurrencyCodes());

        return "convert";
    }

    private String[] getCurrencyCodes() {
        RestTemplate restTemplate = new RestTemplate();

        URI uri = UriComponentsBuilder
                .fromUriString("https://api.currencybeacon.com/v1/latest")
                .queryParam("api_key", "1SMxQumaAcC996SUXGsnlBPW44t2RR82")
                .build()
                .toUri();
        String response = restTemplate.getForObject(uri, String.class);
        List<String> currencyCodes = new ArrayList<>();
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response);

            // Assuming 'rates' object contains currency codes as keys
            JsonNode ratesNode = root.path("rates");

            Iterator<String> fieldNames = ratesNode.fieldNames();
            while (fieldNames.hasNext()) {
                String currencyCode = fieldNames.next();
                currencyCodes.add(currencyCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return currencyCodes.toArray(new String[0]);
    }
}
