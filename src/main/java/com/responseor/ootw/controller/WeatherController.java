package com.responseor.ootw.controller;

import com.responseor.ootw.dto.weather.OpenMeteoWeatherResponseDto;
import com.responseor.ootw.dto.weather.WeatherResponseDto;
import com.responseor.ootw.entity.ClothesByTemp;
import com.responseor.ootw.service.WeatherService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

@RestController
@AllArgsConstructor
@RequestMapping("/weathers")
public class WeatherController {
    private final WeatherService weatherService;

    @GetMapping("")
    private ResponseEntity<WeatherResponseDto> getWeather(@RequestParam("lat") String lat, @RequestParam("lon") String lon) {
        OpenMeteoWeatherResponseDto openMeteoWeatherResponseDto = weatherService.getWeather(lat, lon);
        WeatherResponseDto responseDto = WeatherResponseDto.builder()
                .id(Objects.requireNonNull(openMeteoWeatherResponseDto).getCurrent().getWeather_code())
                .temp(openMeteoWeatherResponseDto.getCurrent().getTemperature_2m())
                .feelsTemp(openMeteoWeatherResponseDto.getCurrent().getApparent_temperature())
                .minTemp(openMeteoWeatherResponseDto.getDaily().getTemperature_2m_min().get(0))
                .maxTemp(openMeteoWeatherResponseDto.getDaily().getTemperature_2m_max().get(0))
                .humidity(openMeteoWeatherResponseDto.getCurrent().getRelative_humidity_2m())
                .windSpeed(openMeteoWeatherResponseDto.getCurrent().getWind_speed_10m())
                .cloudsAll(openMeteoWeatherResponseDto.getCurrent().getCloud_cover())
                .sunrise(openMeteoWeatherResponseDto.getDaily().getSunrise().toString())
                .sunset(openMeteoWeatherResponseDto.getDaily().getSunset().toString())
                .build();

        return ResponseEntity.ok().body(responseDto);
    }

    @GetMapping("/clothes")
    private ResponseEntity<List<ClothesByTemp>> getClothes(@RequestParam("temp") Integer temp) {
        return ResponseEntity.ok().body(weatherService.getClothes(temp));
    }

}
