package br.com.ifba.estoque_api.dto;

import java.math.BigDecimal;
import java.util.List;

public record RelatorioValorEstoqueResponse(
		int totalProdutos,
		int totalItensEmEstoque,
		int produtosComEstoqueBaixo,
		BigDecimal valorTotalCusto,
		BigDecimal valorTotalVenda,
		BigDecimal lucroPotencial,
		List<ValorPorCategoriaResponse> porCategoria
) {
}
