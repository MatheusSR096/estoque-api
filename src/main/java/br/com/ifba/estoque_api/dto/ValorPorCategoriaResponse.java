package br.com.ifba.estoque_api.dto;

import java.math.BigDecimal;

public record ValorPorCategoriaResponse(
		Long categoriaId,
		String categoriaNome,
		int quantidadeProdutos,
		int quantidadeItens,
		BigDecimal valorCusto,
		BigDecimal valorVenda
) {
}
