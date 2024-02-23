package org.hotel.backend.repository;


import org.hotel.backend.dto.WeatherData;

public interface WeatherRepository {
    WeatherData getWeatherData(String city);
}