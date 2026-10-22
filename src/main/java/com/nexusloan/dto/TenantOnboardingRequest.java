package com.nexusloan.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record TenantOnboardingRequest(
        @NotBlank @Size(max = 120) String tenantName,
        @NotBlank
        @Size(min = 3, max = 60)
        @Pattern(regexp = "^[a-z0-9]+(-[a-z0-9]+)*$",
                message = "deve conter apenas letras minúsculas, números e hífens")
        String tenantSlug,
        @NotBlank @Size(max = 120) String adminName,
        @NotBlank @Email @Size(max = 160) String adminEmail,
        @NotBlank @Size(min = 8, max = 72) String adminPassword) {
}
