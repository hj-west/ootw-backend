package com.responseor.ootw.config.properties;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("open-data-api")
@Data
@NoArgsConstructor
public class FineDustProperties {
    private String key;
    private String host;
}
