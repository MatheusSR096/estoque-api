package br.com.ifba.estoque_api.repository;

import br.com.ifba.estoque_api.model.MovimentacaoEstoque;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MovimentacaoEstoqueRepository extends JpaRepository<MovimentacaoEstoque, Long> {

	List<MovimentacaoEstoque> findAllByOrderByDataHoraDesc();

	List<MovimentacaoEstoque> findByProdutoIdOrderByDataHoraDesc(Long produtoId);

	boolean existsByProdutoId(Long produtoId);

	@Query("""
			select m from MovimentacaoEstoque m
			where m.dataHora between :inicio and :fim
			  and (:produtoId is null or m.produto.id = :produtoId)
			order by m.dataHora desc
			""")
	List<MovimentacaoEstoque> buscarPorPeriodo(
			@Param("inicio") LocalDateTime inicio,
			@Param("fim") LocalDateTime fim,
			@Param("produtoId") Long produtoId
	);
}
