package com.mstftrgt.todoapp.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "security.jwt")
public class JwtTokenConfiguration {

    private String secretKey;
    private long expirationTime;
}
