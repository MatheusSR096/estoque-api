package br.com.ifba.estoque_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.ifba.estoque_api.model.Fornecedor;

public interface FornecedorRepository extends JpaRepository<Fornecedor, Long> {

}
