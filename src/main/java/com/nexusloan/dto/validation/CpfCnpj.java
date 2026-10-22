package com.nexusloan.dto.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

/** Aceita CPF ou CNPJ válidos (dígitos verificadores), com ou sem máscara. */
@Documented
@Constraint(validatedBy = CpfCnpjValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.RECORD_COMPONENT})
@Retention(RetentionPolicy.RUNTIME)
public @interface CpfCnpj {

    String message() default "deve ser um CPF ou CNPJ válido";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
