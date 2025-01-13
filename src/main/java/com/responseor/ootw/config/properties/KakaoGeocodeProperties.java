package com.responseor.ootw.config.properties;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("kakao-geocoder")
@Data
@NoArgsConstructor
public class KakaoGeocodeProperties {
    private String host;
    private String restKey;
}
