package com.nexusloan.security;

import java.util.UUID;

import com.nexusloan.enums.Role;

/** Principal da requisição, montado a partir das claims do JWT. */
public record AuthenticatedUser(UUID userId, UUID tenantId, String email, Role role) {
}
