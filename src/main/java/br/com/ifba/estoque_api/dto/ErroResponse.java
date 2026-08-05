package br.com.ifba.estoque_api.dto;

import java.time.LocalDateTime;
import java.util.Map;

public record ErroResponse(
		LocalDateTime timestamp,
		int status,
		String mensagem,
		Map<String, String> campos
) {

	public static ErroResponse of(int status, String mensagem) {
		return new ErroResponse(LocalDateTime.now(), status, mensagem, null);
	}

	public static ErroResponse of(int status, String mensagem, Map<String, String> campos) {
		return new ErroResponse(LocalDateTime.now(), status, mensagem, campos);
	}
}
