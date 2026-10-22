package com.nexusloan.service;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

import com.nexusloan.config.LoanProperties;
import com.nexusloan.enums.LoanStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Regra de aprovação automática: reprova quando {@code amount > monthlyIncome * multiplicador}.
 * O multiplicador vem da configuração {@code nexusloan.loan.max-income-multiplier}.
 */
@Component
@RequiredArgsConstructor
public class LoanApprovalPolicy {

    private static final Locale PT_BR = Locale.of("pt", "BR");

    private final LoanProperties properties;

    public LoanDecision evaluate(BigDecimal amount, BigDecimal monthlyIncome) {
        BigDecimal multiplier = properties.maxIncomeMultiplier();
        BigDecimal limit = monthlyIncome.multiply(multiplier);

        if (amount.compareTo(limit) > 0) {
            return new LoanDecision(LoanStatus.REJECTED, String.format(
                    "Reprovado: valor solicitado de %s excede o limite de %s (%sx a renda mensal de %s).",
                    money(amount), money(limit), multiplier.stripTrailingZeros().toPlainString(), money(monthlyIncome)));
        }
        return new LoanDecision(LoanStatus.APPROVED, String.format(
                "Aprovado: valor solicitado de %s está dentro do limite de %s (%sx a renda mensal de %s).",
                money(amount), money(limit), multiplier.stripTrailingZeros().toPlainString(), money(monthlyIncome)));
    }

    private static String money(BigDecimal value) {
        return NumberFormat.getCurrencyInstance(PT_BR).format(value);
    }
}
