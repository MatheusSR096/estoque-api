package br.com.ifba.estoque_api.dto.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CnpjValidator.class)
public @interface ValidCnpj {
	
	String message() default "CNPJ inválido, deve conter 14 dígitos numéricos";
	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};

}
