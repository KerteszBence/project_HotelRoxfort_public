package org.hotel.backend.service;



import com.fasterxml.jackson.core.JsonProcessingException;

import lombok.extern.slf4j.Slf4j;
import org.hotel.backend.config.ExchangeConfig;
import org.hotel.backend.dto.ExchangeRatesHufEur;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

@Service
@Transactional
@Slf4j
public class ExchangeService {
    private final RestTemplate restTemplate;
    private final ExchangeConfig config;

    @Autowired
    public ExchangeService(RestTemplate restTemplate, ExchangeConfig config) {
        this.restTemplate = restTemplate;
        this.config = config;
    }

    public String getExchangeRates() {
        String apiUrl = config.getApiUrl() + "latest.json?app_id=" + config.getApiKey();
        return restTemplate.getForObject(apiUrl, String.class);
    }

    public ExchangeRatesHufEur getHufEurRates() {
        String json = getExchangeRates();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode rootNode = objectMapper.readTree(json);

            JsonNode ratesNode = rootNode.path("rates");

            double eurRate = ratesNode.path("EUR").asDouble();
            double hufRate = ratesNode.path("HUF").asDouble();

            ExchangeRatesHufEur exchangeRatesHufEur = new ExchangeRatesHufEur();
            exchangeRatesHufEur.setEurRate(eurRate);
            exchangeRatesHufEur.setHufRate(hufRate);

            return exchangeRatesHufEur;

        } catch (IOException e) {
            log.error("IO error while getting HUF-EUR exchange rates");
            throw new ExchangeRateException("IO error getting exchange rates", e);
        }
    }
}