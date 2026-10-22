package com.nexusloan.dto.validation;

import static org.assertj.core.api.Assertions.assertThat;

import com.nexusloan.support.TestDocuments;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class CpfCnpjValidatorTest {

    private final CpfCnpjValidator validator = new CpfCnpjValidator();

    @RepeatedTest(20)
    void acceptsGeneratedCpf() {
        assertThat(validator.isValid(TestDocuments.randomCpf(), null)).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"529.982.247-25", "52998224725", "11.222.333/0001-81", "11222333000181"})
    void acceptsValidDocumentsWithOrWithoutMask(String document) {
        assertThat(validator.isValid(document, null)).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"52998224724", "11222333000182", "11111111111", "00000000000000", "123", ""})
    void rejectsInvalidDocuments(String document) {
        assertThat(validator.isValid(document, null)).isFalse();
    }
}
