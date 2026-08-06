package br.com.ifba.estoque_api.exception;

public class NegocioException extends RuntimeException {

	public NegocioException(String mensagem) {
		super(mensagem);
	}
}
