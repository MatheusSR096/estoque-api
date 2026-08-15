package br.com.ifba.estoque_api.repository;

import br.com.ifba.estoque_api.model.MovimentacaoEstoque;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovimentacaoEstoqueRepository extends JpaRepository<MovimentacaoEstoque, Long> {

	List<MovimentacaoEstoque> findAllByOrderByDataHoraDesc();

	List<MovimentacaoEstoque> findByProdutoIdOrderByDataHoraDesc(Long produtoId);

	boolean existsByProdutoId(Long produtoId);
}
