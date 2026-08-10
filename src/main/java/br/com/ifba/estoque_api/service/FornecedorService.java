package br.com.ifba.estoque_api.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.ifba.estoque_api.dto.FornecedorRequest;
import br.com.ifba.estoque_api.dto.FornecedorResponse;
import br.com.ifba.estoque_api.exception.NegocioException;
import br.com.ifba.estoque_api.exception.RecursoNaoEncontradoException;
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
	
	@Transactional(readOnly = true)
	public FornecedorResponse buscarPorId(Long id) {
		
		return FornecedorResponse.fromEntity(buscarEntidadePorId(id));
		
	}
	
	@Transactional(readOnly = true)
	public FornecedorResponse buscarPorCnpj(String cnpj) {
		
		return FornecedorResponse.fromEntity(buscarEntidadePorCnpj(cnpj));
		
	}
	
	@Transactional
	public FornecedorResponse criar(FornecedorRequest request) {
		
		validarCnpjUnico(request.cnpj(), null);
		
		Fornecedor fornecedor = Fornecedor.builder()
				.nomeFantasia(request.nomeFantasia().trim())
				.cnpj(request.cnpj())
				.telefone(request.telefone())
				.email(request.email())
				.endereco(normalizarEndereco(request.endereco()))
				.build();
		
		return FornecedorResponse.fromEntity(repository.save(fornecedor));
		
	}
	
	@Transactional
	public FornecedorResponse atualizar(Long id, FornecedorRequest request) {
		
		Fornecedor fornecedor = buscarEntidadePorId(id);
		validarCnpjUnico(request.cnpj(), id);
		
		fornecedor.setNomeFantasia(request.nomeFantasia().trim());
		fornecedor.setCnpj(request.cnpj());
		fornecedor.setTelefone(request.telefone());
		fornecedor.setEmail(request.email());
		fornecedor.setEndereco(normalizarEndereco(request.endereco()));
		
		return FornecedorResponse.fromEntity(repository.save(fornecedor));
		
	}
	
	@Transactional
	public void remover(Long id) {
		
		Fornecedor fornecedor = buscarEntidadePorId(id);
		
		repository.delete(fornecedor);
		
	}
	
	@Transactional(readOnly = true)
	public Fornecedor buscarEntidadePorId(Long id) {
		
		return repository.findById(id)
				.orElseThrow(() -> new RecursoNaoEncontradoException("Fornecedor não encontrado com ID: " + id));
		
	}
	
	@Transactional(readOnly = true)
	public Fornecedor buscarEntidadePorCnpj(String cnpj) {
		
		return repository.findByCnpj(cnpj)
				.orElseThrow(() -> new RecursoNaoEncontradoException("Fornecedor não encontrado com CNPJ: " + cnpj));
		
	}
	
	private void validarCnpjUnico(String cnpj, Long id) {
		
		boolean existe = (id == null
				? repository.existsByCnpj(cnpj)
				: repository.existsByCnpjAndIdNot(cnpj, id));
		
		if (existe) {
			throw new NegocioException("Já existe um fornecedor com o CNPJ: " + cnpj);
		}
		
	}
	
	private String normalizarEndereco(String endereco) {
		
		if (endereco == null || endereco.isBlank()) {
			return null;
		}
		
		return endereco.trim();
		
	}


}
