package com.responseor.ootw.service;

import com.responseor.ootw.dto.weather.OpenMeteoWeatherResponseDto;
import com.responseor.ootw.entity.ClothesByTemp;

import java.util.List;

public interface WeatherService {
    OpenMeteoWeatherResponseDto getWeather(String lat, String lon);
    List<ClothesByTemp> getClothes(int temp);
}
