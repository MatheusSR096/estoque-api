package br.com.ifba.estoque_api.dto;

public record MovimentacaoPorProdutoResponse(
		Long produtoId,
		String produtoNome,
		String produtoSku,
		int totalEntradas,
		int totalSaidas,
		int saldoPeriodo
) {
}
