package com.nexusloan.dto.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

/**
 * Limita o tamanho em bytes UTF-8, e não em caracteres. Necessário para senhas: o BCrypt
 * rejeita entradas acima de 72 bytes, e um caractere multibyte (ex.: emoji) ocupa até 4.
 */
@Documented
@Constraint(validatedBy = MaxUtf8BytesValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.RECORD_COMPONENT})
@Retention(RetentionPolicy.RUNTIME)
public @interface MaxUtf8Bytes {

    int value();

    String message() default "deve ter no máximo {value} bytes";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
