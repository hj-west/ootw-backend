package com.responseor.ootw.dto.weather;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OpenMeteoWeatherResponseDto {
    private Current current;
    private Daily daily;


    @Data
    public static class Current {
        private Float temperature_2m;
        private Integer relative_humidity_2m;
        private Float apparent_temperature;
        private Integer weather_code;
        private Integer cloud_cover;
        private Float wind_speed_10m;
    }

    @Data
    public static class Daily {
        private List<Integer> weather_code;
        private List<Float> temperature_2m_max;
        private List<Float> temperature_2m_min;
        private List<Float> apparent_temperature_max;
        private List<Float> apparent_temperature_min;
        private List<String> sunrise;
        private List<String> sunset;
    }

}
