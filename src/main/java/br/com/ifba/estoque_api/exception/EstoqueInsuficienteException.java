package br.com.ifba.estoque_api.exception;

public class EstoqueInsuficienteException extends NegocioException {

	public EstoqueInsuficienteException(String mensagem) {
		super(mensagem);
	}
}
