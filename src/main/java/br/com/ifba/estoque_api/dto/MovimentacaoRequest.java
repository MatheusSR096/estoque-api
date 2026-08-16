package br.com.ifba.estoque_api.dto;

import br.com.ifba.estoque_api.model.TipoMovimentacao;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record MovimentacaoRequest(
		@NotNull(message = "O produto é obrigatório")
		@Positive(message = "O id do produto deve ser maior que zero")
		Long produtoId,

		@NotNull(message = "O tipo da movimentação é obrigatório")
		TipoMovimentacao tipo,

		@NotNull(message = "A quantidade é obrigatória")
		@Positive(message = "A quantidade deve ser maior que zero")
		Integer quantidade,

		@Size(max = 255, message = "O motivo deve ter no máximo 255 caracteres")
		String motivo
) {
}
