package br.com.ifba.estoque_api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.ifba.estoque_api.model.Fornecedor;

public interface FornecedorRepository extends JpaRepository<Fornecedor, Long> {

	public Optional<Fornecedor> findByCnpj(String cnpj);
	
	public boolean existsByCnpj(String cnpj);
	
	public boolean existsByCnpjAndIdNot(String cnpj, Long id);
}
