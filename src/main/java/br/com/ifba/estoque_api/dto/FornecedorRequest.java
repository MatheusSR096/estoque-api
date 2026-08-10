package br.com.ifba.estoque_api.dto;

import br.com.ifba.estoque_api.dto.validation.ValidCnpj;
import br.com.ifba.estoque_api.dto.validation.ValidTelefone;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record FornecedorRequest(
		
		@NotBlank(message = "O nome fantasia do fornecedor é obrigatório")
		String nomeFantasia,
		
		@NotBlank(message = "O CNPJ do fornecedor é obrigatório")
		@Size(max = 14, message = "O CNPJ deve ter no máximo 14 caracteres")
		@ValidCnpj
		String cnpj,
		
		@Size(max = 11, message = "O telefone deve ter no máximo 11 caracteres")
		@ValidTelefone
		String telefone,
		
		@Email
		String email,
		
		@Size(max = 255, message = "O endereço deve ter no máximo 255 caracteres")
		String endereco
) {

}
