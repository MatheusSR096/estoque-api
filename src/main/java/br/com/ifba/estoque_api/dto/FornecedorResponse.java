package br.com.ifba.estoque_api.dto;

import br.com.ifba.estoque_api.model.Fornecedor;

public record FornecedorResponse(
		Long id,
		String nomeFantasia,
		String cnpj,
		String telefone,
		String email,
		String endereco
) {
	
	public static FornecedorResponse fromEntity(Fornecedor fornecedor) {
		return new FornecedorResponse(
				fornecedor.getId(),
				fornecedor.getNomeFantasia(),
				fornecedor.getCnpj(),
				fornecedor.getTelefone(),
				fornecedor.getEmail(),
				fornecedor.getEndereco()
			);
	}
	
}
