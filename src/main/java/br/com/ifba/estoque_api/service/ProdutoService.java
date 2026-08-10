package br.com.ifba.estoque_api.service;

import br.com.ifba.estoque_api.dto.ProdutoRequest;
import br.com.ifba.estoque_api.dto.ProdutoResponse;
import br.com.ifba.estoque_api.exception.NegocioException;
import br.com.ifba.estoque_api.exception.RecursoNaoEncontradoException;
import br.com.ifba.estoque_api.model.Produto;
import br.com.ifba.estoque_api.repository.ProdutoRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProdutoService {

	private final ProdutoRepository produtoRepository;
	private final CategoriaService categoriaService;
	private final FornecedorService fornecedorService;

	@Transactional(readOnly = true)
	public Page<ProdutoResponse> listar(String nome, Long categoriaId, Long fornecedorId, Pageable pageable) {
		return produtoRepository
				.buscarComFiltros(normalizar(nome), categoriaId, fornecedorId, pageable)
				.map(ProdutoResponse::fromEntity);
	}

	@Transactional(readOnly = true)
	public ProdutoResponse buscarPorId(Long id) {
		return ProdutoResponse.fromEntity(buscarEntidadePorId(id));
	}

	@Transactional(readOnly = true)
	public List<ProdutoResponse> listarComEstoqueBaixo() {
		return produtoRepository.buscarComEstoqueBaixo().stream()
				.map(ProdutoResponse::fromEntity)
				.toList();
	}

	@Transactional
	public ProdutoResponse criar(ProdutoRequest request) {
		validarSkuUnico(request.sku(), null);
		validarPrecos(request);

		Produto produto = Produto.builder()
				.nome(request.nome().trim())
				.sku(request.sku().trim().toUpperCase())
				.descricao(normalizar(request.descricao()))
				.precoCusto(request.precoCusto())
				.precoVenda(request.precoVenda())
				.quantidadeAtual(request.quantidadeInicial() == null ? 0 : request.quantidadeInicial())
				.quantidadeMinima(request.quantidadeMinima())
				.categoria(categoriaService.buscarEntidadePorId(request.categoriaId()))
				.fornecedor(fornecedorService.buscarEntidadePorId(request.fornecedorId()))
				.build();

		return ProdutoResponse.fromEntity(produtoRepository.save(produto));
	}

	@Transactional
	public ProdutoResponse atualizar(Long id, ProdutoRequest request) {
		Produto produto = buscarEntidadePorId(id);
		validarSkuUnico(request.sku(), id);
		validarPrecos(request);

		produto.setNome(request.nome().trim());
		produto.setSku(request.sku().trim().toUpperCase());
		produto.setDescricao(normalizar(request.descricao()));
		produto.setPrecoCusto(request.precoCusto());
		produto.setPrecoVenda(request.precoVenda());
		produto.setQuantidadeMinima(request.quantidadeMinima());
		produto.setCategoria(categoriaService.buscarEntidadePorId(request.categoriaId()));
		produto.setFornecedor(fornecedorService.buscarEntidadePorId(request.fornecedorId()));
		// quantidadeAtual não é alterada aqui: só as movimentações mudam o saldo.

		return ProdutoResponse.fromEntity(produtoRepository.save(produto));
	}

	@Transactional
	public void remover(Long id) {
		// ponytail: quando MovimentacaoEstoque existir, bloquear a exclusão de produto com histórico.
		produtoRepository.delete(buscarEntidadePorId(id));
	}

	@Transactional(readOnly = true)
	public Produto buscarEntidadePorId(Long id) {
		return produtoRepository.findById(id)
				.orElseThrow(() -> new RecursoNaoEncontradoException(
						"Produto não encontrado com id: " + id
				));
	}

	private void validarSkuUnico(String sku, Long idAtual) {
		String skuNormalizado = sku.trim().toUpperCase();
		boolean existe = idAtual == null
				? produtoRepository.existsBySkuIgnoreCase(skuNormalizado)
				: produtoRepository.existsBySkuIgnoreCaseAndIdNot(skuNormalizado, idAtual);

		if (existe) {
			throw new NegocioException("Já existe um produto com o SKU: " + skuNormalizado);
		}
	}

	private void validarPrecos(ProdutoRequest request) {
		if (request.precoVenda().compareTo(request.precoCusto()) <= 0) {
			throw new NegocioException("O preço de venda deve ser maior que o preço de custo");
		}
	}

	private String normalizar(String valor) {
		if (valor == null || valor.isBlank()) {
			return null;
		}
		return valor.trim();
	}
}
