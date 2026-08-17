package br.com.ifba.estoque_api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record RelatorioMovimentacoesResponse(
		LocalDate dataInicio,
		LocalDate dataFim,
		int totalMovimentacoes,
		int totalEntradas,
		int totalSaidas,
		int saldoPeriodo,
		BigDecimal valorEntradas,
		BigDecimal valorSaidas,
		List<MovimentacaoPorProdutoResponse> porProduto
) {
}
