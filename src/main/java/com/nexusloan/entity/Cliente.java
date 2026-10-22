package com.nexusloan.entity;

import java.math.BigDecimal;
import java.math.RoundingMode;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "cliente")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Cliente extends TenantScopedEntity {

    private static final int MONEY_SCALE = 2;

    @Column(nullable = false, length = 120)
    private String name;

    /** CPF (11 dígitos) ou CNPJ (14 dígitos), armazenado só com dígitos. */
    @Column(nullable = false, length = 14)
    private String document;

    @Column(nullable = false, length = 160)
    private String email;

    @Column(name = "monthly_income", nullable = false, precision = 15, scale = 2)
    private BigDecimal monthlyIncome;

    public Cliente(String name, String document, String email, BigDecimal monthlyIncome) {
        this.name = name;
        this.document = document;
        this.email = email;
        this.monthlyIncome = monthlyIncome.setScale(MONEY_SCALE, RoundingMode.UNNECESSARY);
    }
}
