package br.com.ifba.estoque_api.dto.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TelefoneValidator implements ConstraintValidator<ValidTelefone, String> {

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		
		if (value == null) return true;
		
		String numeros = value.replaceAll("\\D", "");
		
		return numeros.length() == 10
				|| numeros.length() == 11;
		
	}
	
}
