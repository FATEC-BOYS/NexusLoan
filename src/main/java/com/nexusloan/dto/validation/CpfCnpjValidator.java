package com.nexusloan.dto.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CpfCnpjValidator implements ConstraintValidator<CpfCnpj, String> {

    private static final int CPF_LENGTH = 11;
    private static final int CNPJ_LENGTH = 14;
    private static final int[] CNPJ_WEIGHTS = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // obrigatoriedade fica a cargo do @NotBlank
        }
        String digits = onlyDigits(value);
        return switch (digits.length()) {
            case CPF_LENGTH -> isValidCpf(digits);
            case CNPJ_LENGTH -> isValidCnpj(digits);
            default -> false;
        };
    }

    public static String onlyDigits(String value) {
        return value.replaceAll("\\D", "");
    }

    static boolean isValidCpf(String cpf) {
        if (allSameDigit(cpf)) {
            return false;
        }
        return cpf.charAt(9) - '0' == cpfCheckDigit(cpf, 9)
                && cpf.charAt(10) - '0' == cpfCheckDigit(cpf, 10);
    }

    static boolean isValidCnpj(String cnpj) {
        if (allSameDigit(cnpj)) {
            return false;
        }
        return cnpj.charAt(12) - '0' == cnpjCheckDigit(cnpj, 12)
                && cnpj.charAt(13) - '0' == cnpjCheckDigit(cnpj, 13);
    }

    private static int cpfCheckDigit(String cpf, int length) {
        int sum = 0;
        for (int i = 0; i < length; i++) {
            sum += (cpf.charAt(i) - '0') * (length + 1 - i);
        }
        int rest = (sum * 10) % 11;
        return rest == 10 ? 0 : rest;
    }

    private static int cnpjCheckDigit(String cnpj, int length) {
        int offset = CNPJ_WEIGHTS.length - length;
        int sum = 0;
        for (int i = 0; i < length; i++) {
            sum += (cnpj.charAt(i) - '0') * CNPJ_WEIGHTS[offset + i];
        }
        int rest = sum % 11;
        return rest < 2 ? 0 : 11 - rest;
    }

    private static boolean allSameDigit(String digits) {
        return digits.chars().distinct().count() == 1;
    }
}
