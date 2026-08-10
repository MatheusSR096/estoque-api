package br.com.ifba.estoque_api.dto;

import br.com.ifba.estoque_api.model.Produto;
import java.math.BigDecimal;

public record ProdutoResponse(
		Long id,
		String nome,
		String sku,
		String descricao,
		BigDecimal precoCusto,
		BigDecimal precoVenda,
		Integer quantidadeAtual,
		Integer quantidadeMinima,
		boolean estoqueBaixo,
		CategoriaResponse categoria,
		FornecedorResponse fornecedor
) {

	public static ProdutoResponse fromEntity(Produto produto) {
		return new ProdutoResponse(
				produto.getId(),
				produto.getNome(),
				produto.getSku(),
				produto.getDescricao(),
				produto.getPrecoCusto(),
				produto.getPrecoVenda(),
				produto.getQuantidadeAtual(),
				produto.getQuantidadeMinima(),
				produto.getQuantidadeAtual() <= produto.getQuantidadeMinima(),
				CategoriaResponse.fromEntity(produto.getCategoria()),
				FornecedorResponse.fromEntity(produto.getFornecedor())
		);
	}
}
