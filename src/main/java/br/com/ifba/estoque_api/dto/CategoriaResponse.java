package br.com.ifba.estoque_api.dto;

import br.com.ifba.estoque_api.model.Categoria;

public record CategoriaResponse(
		Long id,
		String nome,
		String descricao
) {

	public static CategoriaResponse fromEntity(Categoria categoria) {
		return new CategoriaResponse(
				categoria.getId(),
				categoria.getNome(),
				categoria.getDescricao()
		);
	}
}
