package br.com.ifba.estoque_api.exception;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import br.com.ifba.estoque_api.dto.ErroResponse;
import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {
	
	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ErroResponse> handleConstraintViolation(ConstraintViolationException ex) {
		
		Map<String, String> campos = new LinkedHashMap<>();
		
		ex.getConstraintViolations().forEach(violation -> {
			String propriedade = violation.getPropertyPath().toString();
			String nomeCampo = propriedade.substring(propriedade.lastIndexOf('.') + 1);
			
			campos.put(nomeCampo, violation.getMessage());
		});
		
		return ResponseEntity
				.status(HttpStatus.BAD_REQUEST)
				.body(ErroResponse.of(HttpStatus.BAD_REQUEST.value(), "Erro de validação no parâmetro da URL", campos));
		
	}

	@ExceptionHandler(RecursoNaoEncontradoException.class)
	public ResponseEntity<ErroResponse> handleRecursoNaoEncontrado(RecursoNaoEncontradoException ex) {
		return ResponseEntity
				.status(HttpStatus.NOT_FOUND)
				.body(ErroResponse.of(HttpStatus.NOT_FOUND.value(), ex.getMessage()));
	}

	@ExceptionHandler(NegocioException.class)
	public ResponseEntity<ErroResponse> handleNegocio(NegocioException ex) {
		return ResponseEntity
				.status(HttpStatus.CONFLICT)
				.body(ErroResponse.of(HttpStatus.CONFLICT.value(), ex.getMessage()));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErroResponse> handleValidacao(MethodArgumentNotValidException ex) {
		Map<String, String> campos = new LinkedHashMap<>();
		for (FieldError error : ex.getBindingResult().getFieldErrors()) {
			campos.put(error.getField(), error.getDefaultMessage());
		}

		return ResponseEntity
				.status(HttpStatus.BAD_REQUEST)
				.body(ErroResponse.of(HttpStatus.BAD_REQUEST.value(), "Erro de validação", campos));
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ErroResponse> handleMensagemInvalida(HttpMessageNotReadableException ex) {
		return ResponseEntity
				.status(HttpStatus.BAD_REQUEST)
				.body(ErroResponse.of(HttpStatus.BAD_REQUEST.value(), "Corpo da requisição inválido ou malformado"));
	}

	@ExceptionHandler(NoResourceFoundException.class)
	public ResponseEntity<ErroResponse> handleRecursoEstaticoNaoEncontrado(NoResourceFoundException ex) {
		return ResponseEntity
				.status(HttpStatus.NOT_FOUND)
				.body(ErroResponse.of(HttpStatus.NOT_FOUND.value(), "Recurso não encontrado"));
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<ErroResponse> handleParametroInvalido(MethodArgumentTypeMismatchException ex) {
		String esperado = ex.getRequiredType() == LocalDate.class
				? "informe a data no formato aaaa-mm-dd (ex.: 2026-08-16)"
				: "valor inválido para o parâmetro";

		return ResponseEntity
				.status(HttpStatus.BAD_REQUEST)
				.body(ErroResponse.of(HttpStatus.BAD_REQUEST.value(),
						"Parâmetro inválido na URL", Map.of(ex.getName(), esperado)));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErroResponse> handleGenerico(Exception ex) {
		return ResponseEntity
				.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(ErroResponse.of(
						HttpStatus.INTERNAL_SERVER_ERROR.value(),
						"Erro interno no servidor"
				));
	}
}
