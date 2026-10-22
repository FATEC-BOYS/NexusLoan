package com.nexusloan.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import com.nexusloan.entity.Emprestimo;
import com.nexusloan.enums.LoanStatus;

public record EmprestimoResponse(
        UUID id,
        UUID clienteId,
        BigDecimal amount,
        LoanStatus status,
        String reason,
        Instant createdAt,
        Instant updatedAt) {

    public static EmprestimoResponse from(Emprestimo emprestimo) {
        return new EmprestimoResponse(
                emprestimo.getId(),
                emprestimo.getCliente().getId(),
                emprestimo.getAmount(),
                emprestimo.getStatus(),
                emprestimo.getReason(),
                emprestimo.getCreatedAt(),
                emprestimo.getUpdatedAt());
    }
}
