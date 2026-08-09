package br.com.ifba.estoque_api.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.ifba.estoque_api.dto.FornecedorRequest;
import br.com.ifba.estoque_api.dto.FornecedorResponse;
import br.com.ifba.estoque_api.model.Fornecedor;
import br.com.ifba.estoque_api.repository.FornecedorRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FornecedorService {
	
	private final FornecedorRepository repository;
	
	@Transactional(readOnly = true)
	public List<FornecedorResponse> listar() {
		
		return repository.findAll().stream()
				.map(FornecedorResponse::fromEntity)
				.toList();
		
	}
	
	@Transactional
	public FornecedorResponse criar(FornecedorRequest request) {
		
		Fornecedor fornecedor = Fornecedor.builder()
				.nomeFantasia(request.nomeFantasia().trim())
				.cnpj(request.cnpj())
				.telefone(request.telefone())
				.email(request.email())
				.endereco(request.endereco())
				.build();
		
		return FornecedorResponse.fromEntity(repository.save(fornecedor));
		
	}


}
