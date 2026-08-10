package br.com.ifba.estoque_api.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "fornecedores")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Fornecedor {
	
	/*
	 * ADICIONAR VALIDAÇÃO PARA O TELEFONE
	 * */
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotBlank(message = "O nome fantasia do fornecedor é obrigatório")
	private String nomeFantasia;
	
	@NotBlank(message = "O CNPJ do fornecedor é obrigatório")
	@Size(max = 14, message = "O CNPJ deve ter no máximo 14 caracteres")
	@Column(nullable = false, unique = true, length = 14)
	private String cnpj;
	
	@Size(max = 11, message = "O telefone deve ter no máximo 11 caracteres")
	@Column(length = 11)
	private String telefone;
	
	@Email
	private String email;
	
	@Size(max = 255, message = "O endereço deve ter no máximo 255 caracteres")
	@Column(length = 255)
	private String endereco;

}
