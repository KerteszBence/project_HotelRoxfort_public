package org.hotel.backend.dto;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WeatherDataInfo {
    private String description;
    private String dt_txt;
    private double temp;
    private double feels_like;
    private double temp_min;
    private double temp_max;
}