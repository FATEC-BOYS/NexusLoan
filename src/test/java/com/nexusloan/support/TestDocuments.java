package com.nexusloan.support;

import java.util.concurrent.ThreadLocalRandom;

/** Gera CPFs válidos e aleatórios para os testes, sem depender de documentos fixos. */
public final class TestDocuments {

    private TestDocuments() {
    }

    public static String randomCpf() {
        int[] digits = new int[11];
        do {
            for (int i = 0; i < 9; i++) {
                digits[i] = ThreadLocalRandom.current().nextInt(10);
            }
        } while (allEqual(digits));
        digits[9] = checkDigit(digits, 9);
        digits[10] = checkDigit(digits, 10);
        StringBuilder sb = new StringBuilder();
        for (int d : digits) {
            sb.append(d);
        }
        return sb.toString();
    }

    private static int checkDigit(int[] digits, int length) {
        int sum = 0;
        for (int i = 0; i < length; i++) {
            sum += digits[i] * (length + 1 - i);
        }
        int mod = sum % 11;
        return mod < 2 ? 0 : 11 - mod;
    }

    private static boolean allEqual(int[] digits) {
        for (int i = 1; i < 9; i++) {
            if (digits[i] != digits[0]) {
                return false;
            }
        }
        return true;
    }
}
