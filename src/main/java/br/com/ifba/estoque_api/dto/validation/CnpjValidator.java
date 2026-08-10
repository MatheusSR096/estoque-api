package br.com.ifba.estoque_api.dto.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CnpjValidator implements ConstraintValidator<ValidCnpj, String> {

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		
		if (value == null) return false;
		
		String numeros = value.replaceAll("\\D", "");
		return numeros.length() == 14;
		
	}
	
}
