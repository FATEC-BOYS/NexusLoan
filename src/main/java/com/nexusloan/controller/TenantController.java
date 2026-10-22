package com.nexusloan.controller;

import com.nexusloan.dto.TenantOnboardingRequest;
import com.nexusloan.dto.TenantOnboardingResponse;
import com.nexusloan.service.TenantOnboardingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** Onboarding público: cria a organização (tenant) e o seu primeiro ADMIN. */
@RestController
@RequestMapping("/api/tenants")
@RequiredArgsConstructor
public class TenantController {

    private final TenantOnboardingService onboardingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TenantOnboardingResponse onboard(@Valid @RequestBody TenantOnboardingRequest request) {
        return onboardingService.onboard(request);
    }
}
