package com.example.it_iap.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import vn.payos.PayOS;
import vn.payos.core.ClientOptions;

@Configuration
public class PayOSConfig {
    @Value("${payos.payout-client-id}")
    private String clientId;

    @Value("${payos.payout-api-key}")
    private String apiKey;

    @Value("${payos.payout-checksum-key}")
    private String checksumKey;

    @Value("${payos.log-level:NONE}")
    private String logLevel;

    @Bean
    public PayOS payOS() {
        ClientOptions options =
                ClientOptions.builder()
                        .clientId(clientId)
                        .apiKey(apiKey)
                        .checksumKey(checksumKey)
                        .logLevel(ClientOptions.LogLevel.valueOf(logLevel.toUpperCase()))
                        .build();
        return new PayOS(options);
    }
}
