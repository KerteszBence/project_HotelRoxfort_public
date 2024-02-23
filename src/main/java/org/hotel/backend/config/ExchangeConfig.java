package org.hotel.backend.config;


import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Getter
@Configuration
public class ExchangeConfig {

    @Value("${openexchangerates.api.key}")
    private String apiKey;

    @Value("${openexchangerates.api.url}")
    private String apiUrl;

    @Bean(name = "exchangeRestTemplate")
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}