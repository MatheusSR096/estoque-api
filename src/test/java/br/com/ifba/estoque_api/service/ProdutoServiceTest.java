package br.com.ifba.estoque_api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.com.ifba.estoque_api.dto.ProdutoRequest;
import br.com.ifba.estoque_api.dto.ProdutoResponse;
import br.com.ifba.estoque_api.exception.NegocioException;
import br.com.ifba.estoque_api.exception.RecursoNaoEncontradoException;
import br.com.ifba.estoque_api.model.Categoria;
import br.com.ifba.estoque_api.model.Fornecedor;
import br.com.ifba.estoque_api.model.Produto;
import br.com.ifba.estoque_api.repository.ProdutoRepository;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ProdutoServiceTest {

	@Mock
	private ProdutoRepository produtoRepository;

	@Mock
	private CategoriaService categoriaService;

	@Mock
	private FornecedorService fornecedorService;

	@InjectMocks
	private ProdutoService service;

	private ProdutoRequest request(String sku, String custo, String venda) {
		return new ProdutoRequest(
				"Teclado", sku, "  ", new BigDecimal(custo), new BigDecimal(venda),
				5, 10, 1L, 1L
		);
	}

	private Produto produto(int atual, int minima) {
		return Produto.builder()
				.id(1L)
				.nome("Teclado")
				.sku("TEC-01")
				.precoCusto(new BigDecimal("10.00"))
				.precoVenda(new BigDecimal("25.00"))
				.quantidadeAtual(atual)
				.quantidadeMinima(minima)
				.categoria(Categoria.builder().id(1L).nome("Periféricos").build())
				.fornecedor(Fornecedor.builder().id(1L).nomeFantasia("Acme").cnpj("12345678000199").build())
				.build();
	}

	@Test
	public void deveCriarProdutoNormalizandoSkuEQuantidade() {
		when(produtoRepository.existsBySkuIgnoreCase("TEC-01")).thenReturn(false);
		when(categoriaService.buscarEntidadePorId(1L)).thenReturn(produto(0, 0).getCategoria());
		when(fornecedorService.buscarEntidadePorId(1L)).thenReturn(produto(0, 0).getFornecedor());
		when(produtoRepository.save(any(Produto.class))).thenAnswer(i -> i.getArgument(0));

		ProdutoResponse response = service.criar(request(" tec-01 ", "10.00", "25.00"));

		assertEquals("TEC-01", response.sku());
		assertEquals(5, response.quantidadeAtual());
		assertEquals(null, response.descricao());
	}

	@Test
	public void deveRejeitarSkuDuplicado() {
		when(produtoRepository.existsBySkuIgnoreCase("TEC-01")).thenReturn(true);

		assertThrows(NegocioException.class, () -> service.criar(request("TEC-01", "10.00", "25.00")));
		verify(produtoRepository, never()).save(any());
	}

	@Test
	public void deveRejeitarPrecoVendaMenorOuIgualAoCusto() {
		when(produtoRepository.existsBySkuIgnoreCase("TEC-01")).thenReturn(false);

		assertThrows(NegocioException.class, () -> service.criar(request("TEC-01", "25.00", "25.00")));
		verify(produtoRepository, never()).save(any());
	}

	@Test
	public void deveLancar404QuandoProdutoNaoExistir() {
		when(produtoRepository.findById(99L)).thenReturn(Optional.empty());

		assertThrows(RecursoNaoEncontradoException.class, () -> service.buscarPorId(99L));
	}

	@Test
	public void naoDeveAlterarQuantidadeAtualNaAtualizacao() {
		when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto(42, 10)));
		when(produtoRepository.existsBySkuIgnoreCaseAndIdNot("TEC-01", 1L)).thenReturn(false);
		when(categoriaService.buscarEntidadePorId(1L)).thenReturn(produto(0, 0).getCategoria());
		when(fornecedorService.buscarEntidadePorId(1L)).thenReturn(produto(0, 0).getFornecedor());
		when(produtoRepository.save(any(Produto.class))).thenAnswer(i -> i.getArgument(0));

		ProdutoResponse response = service.atualizar(1L, request("TEC-01", "10.00", "25.00"));

		assertEquals(42, response.quantidadeAtual());
	}

	@Test
	public void deveMarcarEstoqueBaixoQuandoAtualMenorOuIgualAoMinimo() {
		assertTrue(ProdutoResponse.fromEntity(produto(10, 10)).estoqueBaixo());
		assertTrue(!ProdutoResponse.fromEntity(produto(11, 10)).estoqueBaixo());
	}
}
