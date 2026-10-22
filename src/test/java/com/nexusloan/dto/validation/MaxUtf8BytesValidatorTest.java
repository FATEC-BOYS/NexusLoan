package com.nexusloan.dto.validation;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.annotation.Annotation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MaxUtf8BytesValidatorTest {

    private final MaxUtf8BytesValidator validator = new MaxUtf8BytesValidator();

    @BeforeEach
    void init() {
        validator.initialize(limit(72));
    }

    @Test
    void acceptsAsciiUpToLimit() {
        assertThat(validator.isValid("a".repeat(72), null)).isTrue();
        assertThat(validator.isValid("a".repeat(73), null)).isFalse();
    }

    @Test
    void countsBytesNotCharacters() {
        String emojis = "😀".repeat(19); // 38 chars, 76 bytes

        assertThat(emojis).hasSize(38);
        assertThat(validator.isValid(emojis, null)).isFalse();
        assertThat(validator.isValid("😀".repeat(18), null)).isTrue(); // 72 bytes
    }

    @Test
    void ignoresNull() {
        assertThat(validator.isValid(null, null)).isTrue();
    }

    private static MaxUtf8Bytes limit(int value) {
        return new MaxUtf8Bytes() {
            @Override public int value() { return value; }
            @Override public String message() { return ""; }
            @Override public Class<?>[] groups() { return new Class<?>[0]; }
            @SuppressWarnings("unchecked")
            @Override public Class<? extends jakarta.validation.Payload>[] payload() { return new Class[0]; }
            @Override public Class<? extends Annotation> annotationType() { return MaxUtf8Bytes.class; }
        };
    }
}
