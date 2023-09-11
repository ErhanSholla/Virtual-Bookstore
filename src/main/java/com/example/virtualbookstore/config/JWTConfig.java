package com.example.virtualbookstore.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("jwt")
@Getter
@Setter
public class JWTConfig {
    private String signature;
    private String issuer;
}
