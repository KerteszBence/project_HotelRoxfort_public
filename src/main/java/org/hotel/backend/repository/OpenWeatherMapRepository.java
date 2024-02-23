package org.hotel.backend.repository;


import org.hotel.backend.dto.WeatherData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

@Repository
public class OpenWeatherMapRepository implements WeatherRepository {

    private final RestTemplate restTemplate;
    private final String apiKey;
    private final String apiUrl = "https://api.openweathermap.org/data/2.5/forecast";

    @Autowired
    public OpenWeatherMapRepository(RestTemplate restTemplate, @Value("${openweathermap.api.key}") String apiKey) {
        this.restTemplate = restTemplate;
        this.apiKey = apiKey;
    }

    @Override
    public WeatherData getWeatherData(String city) {
        System.out.println(apiUrl + "?q=" + city + "&appid=" + apiKey + "&units=metric");
        return restTemplate.getForObject(apiUrl + "?q=" + city + "&appid=" + apiKey + "&units=metric", WeatherData.class);
    }
}

