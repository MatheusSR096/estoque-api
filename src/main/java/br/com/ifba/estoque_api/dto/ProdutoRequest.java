package br.com.ifba.estoque_api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record ProdutoRequest(
		@NotBlank(message = "O nome do produto é obrigatório")
		@Size(max = 100, message = "O nome deve ter no máximo 100 caracteres")
		String nome,

		@NotBlank(message = "O SKU é obrigatório")
		@Size(max = 50, message = "O SKU deve ter no máximo 50 caracteres")
		String sku,

		@Size(max = 255, message = "A descrição deve ter no máximo 255 caracteres")
		String descricao,

		@NotNull(message = "O preço de custo é obrigatório")
		@PositiveOrZero(message = "O preço de custo não pode ser negativo")
		BigDecimal precoCusto,

		@NotNull(message = "O preço de venda é obrigatório")
		@Positive(message = "O preço de venda deve ser maior que zero")
		BigDecimal precoVenda,

		// Usada apenas na criação; em atualizações o saldo vem das movimentações.
		@PositiveOrZero(message = "A quantidade inicial não pode ser negativa")
		Integer quantidadeInicial,

		@NotNull(message = "A quantidade mínima é obrigatória")
		@PositiveOrZero(message = "A quantidade mínima não pode ser negativa")
		Integer quantidadeMinima,

		@NotNull(message = "A categoria é obrigatória")
		Long categoriaId,

		@NotNull(message = "O fornecedor é obrigatório")
		Long fornecedorId
) {
}
