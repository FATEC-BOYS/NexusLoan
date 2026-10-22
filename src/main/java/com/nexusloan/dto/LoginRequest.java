package com.nexusloan.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/** {@code tenant} é o slug da organização (equivalente ao "workspace" de um SaaS). */
public record LoginRequest(
        @NotBlank String tenant,
        @NotBlank @Email String email,
        @NotBlank String password) {
}
