package com.nexusloan.entity;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.nexusloan.enums.LoanStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "emprestimo")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Emprestimo extends TenantScopedEntity {

    private static final int MONEY_SCALE = 2;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cliente_id", nullable = false, updatable = false)
    private Cliente cliente;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private LoanStatus status;

    @Column(length = 255)
    private String reason;

    public Emprestimo(Cliente cliente, BigDecimal amount) {
        this.cliente = cliente;
        this.amount = amount.setScale(MONEY_SCALE, RoundingMode.UNNECESSARY);
        this.status = LoanStatus.PENDING;
    }

    public void decide(LoanStatus status, String reason) {
        if (this.status != LoanStatus.PENDING) {
            throw new IllegalStateException("Empréstimo já foi decidido: " + this.status);
        }
        if (status == LoanStatus.PENDING) {
            throw new IllegalArgumentException("Decisão deve ser APPROVED ou REJECTED");
        }
        this.status = status;
        this.reason = reason;
    }
}
