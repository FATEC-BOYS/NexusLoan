package com.nexusloan.config;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "nexusloan.loan")
public record LoanProperties(@NotNull @Positive BigDecimal maxIncomeMultiplier) {
}
