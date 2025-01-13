package com.responseor.ootw.service;

import com.responseor.ootw.config.exception.CustomException;
import com.responseor.ootw.config.exception.ErrorCode;
import com.responseor.ootw.config.properties.FineDustProperties;
import com.responseor.ootw.config.properties.KakaoGeocodeProperties;
import com.responseor.ootw.dto.auth.CustomUserDetails;
import com.responseor.ootw.dto.weather.KakaoGeocoderResponseDto;
import com.responseor.ootw.dto.weather.OpenMeteoWeatherResponseDto;
import com.responseor.ootw.entity.ClothesByTemp;
import com.responseor.ootw.entity.Member;
import com.responseor.ootw.entity.enums.ViewDefault;
import com.responseor.ootw.repository.ClothesByTempRepository;
import com.responseor.ootw.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URLEncoder;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class WeatherServiceImpl implements WeatherService {

    @Value("${openmeteo.host}")
    private String weatherHost;

    private final FineDustProperties fineDustProperties;
    private final KakaoGeocodeProperties kakaoGeocodeProperties;

    private final ClothesByTempRepository clothesByTempRepository;
    private final MemberRepository memberRepository;

    @Override
    public OpenMeteoWeatherResponseDto getWeather(String lat, String lon) {
        StringBuilder urlBuilder = new StringBuilder(weatherHost);

        OpenMeteoWeatherResponseDto weatherResponseDto;
        try {
            urlBuilder.append("?" + URLEncoder.encode("latitude", "UTF-8") + "=" + lat);
            urlBuilder.append("&" + URLEncoder.encode("longitude", "UTF-8") + "=" + lon);
            urlBuilder.append("&" + URLEncoder.encode("current", "UTF-8") + "=temperature_2m,relative_humidity_2m,apparent_temperature,precipitation,weather_code,cloud_cover,wind_speed_10m");
            urlBuilder.append("&" + URLEncoder.encode("wind_speed_unit", "UTF-8") + "=ms");
            urlBuilder.append("&" + URLEncoder.encode("daily", "UTF-8") + "=weather_code,temperature_2m_max,temperature_2m_min,apparent_temperature_max,apparent_temperature_min,sunrise,sunset");
            urlBuilder.append("&" + URLEncoder.encode("timezone", "UTF-8") + "=Asia/Seoul");
            urlBuilder.append("&" + URLEncoder.encode("forecast_days", "UTF-8") + "=1");

            RestTemplate restTemplate = new RestTemplate();

            weatherResponseDto = restTemplate.getForObject(urlBuilder.toString(), OpenMeteoWeatherResponseDto.class);

            if (weatherResponseDto == null) {
                log.error("WEATHER_API_ERROR : weatherResponseDto == null");
                throw new CustomException(ErrorCode.WEATHER_API_ERROR);
            }

            return weatherResponseDto;

        } catch (Exception e) {
            log.error("WEATHER_API_ERROR : {}",e.getMessage(), e);
            throw new CustomException(ErrorCode.WEATHER_API_ERROR);
        }
    }

    @Override
    public List<ClothesByTemp> getClothes(int temp) {
        Long uuid = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUuid();
        Member member = memberRepository.findByUuid(uuid).orElseThrow(() -> new CustomException(ErrorCode.INCORRECT_MEMBER_INFORMATION));

        List<ClothesByTemp> defaultClothesByTemp = clothesByTempRepository.findByUuidIsNullAndStartTempLessThanEqualOrEndTempGreaterThanEqual(temp);
        List<ClothesByTemp> userClothesByTempList = clothesByTempRepository.findByUuidAndStartTempLessThanEqualOrEndTempGreaterThanEqual(uuid, temp);

        if (ViewDefault.Y.equals(member.getViewDefault())) {
            userClothesByTempList.addAll(defaultClothesByTemp);
        }
        return userClothesByTempList;
    }

    private Object getCityByLocation(Double lon, Double lat) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "KakaoAK "+ kakaoGeocodeProperties.getRestKey());

        HttpEntity request = new HttpEntity(headers);
        RestTemplate restTemplate = new RestTemplate();

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(kakaoGeocodeProperties.getHost())
                .queryParam("x", lon)
                .queryParam("y", lat);

        ResponseEntity<KakaoGeocoderResponseDto> response = restTemplate.exchange(
                uriBuilder.toUriString(),
                HttpMethod.GET,
                request,
                KakaoGeocoderResponseDto.class
        );

        return response;

    }
}

