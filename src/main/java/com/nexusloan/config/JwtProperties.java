package com.nexusloan.config;

import java.time.Duration;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "nexusloan.security.jwt")
public record JwtProperties(
        @NotBlank String secret,
        @NotNull Duration expiration,
        @NotBlank String issuer) {
}
