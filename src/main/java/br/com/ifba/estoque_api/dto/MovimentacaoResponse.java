package br.com.ifba.estoque_api.dto;

import br.com.ifba.estoque_api.model.MovimentacaoEstoque;
import br.com.ifba.estoque_api.model.TipoMovimentacao;
import java.time.LocalDateTime;

public record MovimentacaoResponse(
		Long id,
		Long produtoId,
		String produtoNome,
		String produtoSku,
		TipoMovimentacao tipo,
		Integer quantidade,
		LocalDateTime dataHora,
		String motivo
) {

	public static MovimentacaoResponse fromEntity(MovimentacaoEstoque movimentacao) {
		return new MovimentacaoResponse(
				movimentacao.getId(),
				movimentacao.getProduto().getId(),
				movimentacao.getProduto().getNome(),
				movimentacao.getProduto().getSku(),
				movimentacao.getTipo(),
				movimentacao.getQuantidade(),
				movimentacao.getDataHora(),
				movimentacao.getMotivo()
		);
	}
}
