package br.com.ifba.estoque_api.repository;

import br.com.ifba.estoque_api.model.Produto;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {

	boolean existsBySkuIgnoreCase(String sku);

	boolean existsBySkuIgnoreCaseAndIdNot(String sku, Long id);

	boolean existsByCategoriaId(Long categoriaId);

	boolean existsByFornecedorId(Long fornecedorId);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select p from Produto p where p.id = :id")
	Optional<Produto> buscarPorIdComLock(@Param("id") Long id);

	// ponytail: filtros opcionais em uma query só; troca por Specification se virarem muitos.
	@Query("""
			select p from Produto p
			where (:nome is null or lower(p.nome) like lower(concat('%', :nome, '%')))
			  and (:categoriaId is null or p.categoria.id = :categoriaId)
			  and (:fornecedorId is null or p.fornecedor.id = :fornecedorId)
			""")
	Page<Produto> buscarComFiltros(
			@Param("nome") String nome,
			@Param("categoriaId") Long categoriaId,
			@Param("fornecedorId") Long fornecedorId,
			Pageable pageable
	);

	@Query("select p from Produto p where p.quantidadeAtual <= p.quantidadeMinima order by p.nome")
	List<Produto> buscarComEstoqueBaixo();
}
