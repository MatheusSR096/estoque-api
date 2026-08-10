package br.com.ifba.estoque_api.dto.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = TelefoneValidator.class)
public @interface ValidTelefone {
	
	String message() default "Telefone inválido, deve conter até 11 dígitos numéricos";
	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};

}
