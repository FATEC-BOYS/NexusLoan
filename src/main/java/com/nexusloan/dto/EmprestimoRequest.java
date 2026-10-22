package com.nexusloan.dto;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

public record EmprestimoRequest(
        @NotNull UUID clienteId,
        @NotNull @DecimalMin("0.01") @Digits(integer = 13, fraction = 2) BigDecimal amount) {
}
