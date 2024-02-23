package org.hotel.backend.service;


import org.hotel.backend.dto.WeatherData;
import org.hotel.backend.dto.WeatherDataInfo;
import org.hotel.backend.dto.WeatherDetails;
import org.hotel.backend.repository.WeatherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class WeatherService {

    private final WeatherRepository weatherRepository;

    @Autowired
    public WeatherService(WeatherRepository weatherRepository) {
        this.weatherRepository = weatherRepository;
    }

    public WeatherDataInfo getWeatherData(String city) {
        WeatherData weatherData = weatherRepository.getWeatherData(city);
        WeatherDataInfo weatherDataInfo = new WeatherDataInfo();
        int dateIndex = getNumberForNextThreeDays(weatherData.getList());
        weatherDataInfo.setDt_txt(weatherData.getList().get(dateIndex).getDt_txt());
        weatherDataInfo.setTemp(weatherData.getList().get(dateIndex).getMain().getTemp());
        weatherDataInfo.setFeels_like(weatherData.getList().get(dateIndex).getMain().getFeels_like());
        weatherDataInfo.setTemp_min(weatherData.getList().get(dateIndex).getMain().getTemp_min());
        weatherDataInfo.setTemp_max(weatherData.getList().get(dateIndex).getMain().getTemp_max());
        weatherDataInfo.setDescription(weatherData.getList().get(dateIndex).getWeather().get(0).getDescription());
        return weatherDataInfo;
    }

    public int getNumberForNextThreeDays(List<WeatherDetails> weatherDetailsList) {
        LocalDate today = LocalDate.now();
        LocalDate targetDate = today.plusDays(3);
        String date = targetDate + " 15:00:00";
        System.out.println("date " + date);
        for (WeatherDetails weatherDetails : weatherDetailsList) {
            String weatherDetailsDate = weatherDetails.getDt_txt();
            if (weatherDetailsDate.equals(date)) {
                return weatherDetailsList.indexOf(weatherDetails);
            }
        }
        return -1;
    }
}