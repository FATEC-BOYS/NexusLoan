package com.nexusloan.dto;

import java.util.UUID;

public record TenantOnboardingResponse(UUID tenantId, String tenantName, String tenantSlug, UserResponse admin) {
}
