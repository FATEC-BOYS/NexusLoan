package com.nexusloan.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import com.nexusloan.entity.Cliente;

public record ClienteResponse(
        UUID id,
        String name,
        String document,
        String email,
        BigDecimal monthlyIncome,
        Instant createdAt,
        Instant updatedAt) {

    public static ClienteResponse from(Cliente cliente) {
        return new ClienteResponse(
                cliente.getId(),
                cliente.getName(),
                cliente.getDocument(),
                cliente.getEmail(),
                cliente.getMonthlyIncome(),
                cliente.getCreatedAt(),
                cliente.getUpdatedAt());
    }
}
