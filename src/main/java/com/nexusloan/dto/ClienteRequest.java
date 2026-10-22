package com.nexusloan.dto;

import java.math.BigDecimal;

import com.nexusloan.dto.validation.CpfCnpj;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ClienteRequest(
        @NotBlank @Size(max = 120) String name,
        @NotBlank @CpfCnpj String document,
        @NotBlank @Email @Size(max = 160) String email,
        @NotNull @DecimalMin("0.00") @Digits(integer = 13, fraction = 2) BigDecimal monthlyIncome) {
}
