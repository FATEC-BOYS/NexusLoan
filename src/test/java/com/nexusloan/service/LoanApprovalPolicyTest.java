package com.nexusloan.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import com.nexusloan.config.LoanProperties;
import com.nexusloan.enums.LoanStatus;
import org.junit.jupiter.api.Test;

class LoanApprovalPolicyTest {

    private final LoanApprovalPolicy policy = new LoanApprovalPolicy(new LoanProperties(BigDecimal.TEN));

    @Test
    void approvesWhenAmountIsBelowLimit() {
        LoanDecision decision = policy.evaluate(new BigDecimal("30000.00"), new BigDecimal("5000.00"));

        assertThat(decision.status()).isEqualTo(LoanStatus.APPROVED);
        assertThat(decision.reason()).startsWith("Aprovado").contains("50.000,00");
    }

    @Test
    void approvesWhenAmountEqualsLimit() {
        LoanDecision decision = policy.evaluate(new BigDecimal("50000.00"), new BigDecimal("5000.00"));

        assertThat(decision.status()).isEqualTo(LoanStatus.APPROVED);
    }

    @Test
    void rejectsWhenAmountExceedsLimit() {
        LoanDecision decision = policy.evaluate(new BigDecimal("50000.01"), new BigDecimal("5000.00"));

        assertThat(decision.status()).isEqualTo(LoanStatus.REJECTED);
        assertThat(decision.reason()).startsWith("Reprovado").contains("10x");
    }

    @Test
    void rejectsAnyAmountWhenIncomeIsZero() {
        LoanDecision decision = policy.evaluate(new BigDecimal("0.01"), BigDecimal.ZERO);

        assertThat(decision.status()).isEqualTo(LoanStatus.REJECTED);
    }

    @Test
    void usesConfiguredMultiplier() {
        LoanApprovalPolicy strict = new LoanApprovalPolicy(new LoanProperties(new BigDecimal("2")));

        LoanDecision decision = strict.evaluate(new BigDecimal("10001.00"), new BigDecimal("5000.00"));

        assertThat(decision.status()).isEqualTo(LoanStatus.REJECTED);
        assertThat(decision.reason()).contains("2x");
    }
}
