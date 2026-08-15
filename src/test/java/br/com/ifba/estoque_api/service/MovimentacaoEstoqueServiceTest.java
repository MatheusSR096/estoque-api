package br.com.ifba.estoque_api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.com.ifba.estoque_api.dto.MovimentacaoRequest;
import br.com.ifba.estoque_api.dto.MovimentacaoResponse;
import br.com.ifba.estoque_api.exception.EstoqueInsuficienteException;
import br.com.ifba.estoque_api.exception.NegocioException;
import br.com.ifba.estoque_api.exception.RecursoNaoEncontradoException;
import br.com.ifba.estoque_api.model.MovimentacaoEstoque;
import br.com.ifba.estoque_api.model.Produto;
import br.com.ifba.estoque_api.model.TipoMovimentacao;
import br.com.ifba.estoque_api.repository.MovimentacaoEstoqueRepository;
import br.com.ifba.estoque_api.repository.ProdutoRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MovimentacaoEstoqueServiceTest {

	@Mock
	private MovimentacaoEstoqueRepository movimentacaoRepository;

	@Mock
	private ProdutoRepository produtoRepository;

	@InjectMocks
	private MovimentacaoEstoqueService service;

	@Test
	void deveRegistrarEntradaEAtualizarSaldo() {
		Produto produto = produtoComSaldo(10);
		MovimentacaoRequest request = request(TipoMovimentacao.ENTRADA, 5);
		when(produtoRepository.buscarPorIdComLock(1L)).thenReturn(Optional.of(produto));
		when(movimentacaoRepository.save(any(MovimentacaoEstoque.class)))
				.thenAnswer(invocation -> invocation.getArgument(0));

		MovimentacaoResponse response = service.registrar(request);

		assertEquals(15, produto.getQuantidadeAtual());
		assertEquals(TipoMovimentacao.ENTRADA, response.tipo());
		assertEquals("Compra", response.motivo());
		assertNotNull(response.dataHora());
		verify(produtoRepository).save(produto);
		verify(movimentacaoRepository).save(any(MovimentacaoEstoque.class));
	}

	@Test
	void deveRegistrarSaidaEAtualizarSaldo() {
		Produto produto = produtoComSaldo(10);
		when(produtoRepository.buscarPorIdComLock(1L)).thenReturn(Optional.of(produto));
		when(movimentacaoRepository.save(any(MovimentacaoEstoque.class)))
				.thenAnswer(invocation -> invocation.getArgument(0));

		service.registrar(request(TipoMovimentacao.SAIDA, 4));

		assertEquals(6, produto.getQuantidadeAtual());
		verify(produtoRepository).save(produto);
	}

	@Test
	void devePermitirSaidaIgualAoSaldo() {
		Produto produto = produtoComSaldo(10);
		when(produtoRepository.buscarPorIdComLock(1L)).thenReturn(Optional.of(produto));
		when(movimentacaoRepository.save(any(MovimentacaoEstoque.class)))
				.thenAnswer(invocation -> invocation.getArgument(0));

		service.registrar(request(TipoMovimentacao.SAIDA, 10));

		assertEquals(0, produto.getQuantidadeAtual());
	}

	@Test
	void deveRejeitarSaidaMaiorQueSaldo() {
		Produto produto = produtoComSaldo(3);
		when(produtoRepository.buscarPorIdComLock(1L)).thenReturn(Optional.of(produto));

		assertThrows(
				EstoqueInsuficienteException.class,
				() -> service.registrar(request(TipoMovimentacao.SAIDA, 4))
		);

		assertEquals(3, produto.getQuantidadeAtual());
		verify(produtoRepository, never()).save(any());
		verify(movimentacaoRepository, never()).save(any());
	}

	@Test
	void deveRejeitarQuantidadeZero() {
		assertThrows(
				NegocioException.class,
				() -> service.registrar(request(TipoMovimentacao.ENTRADA, 0))
		);

		verify(produtoRepository, never()).buscarPorIdComLock(any());
		verify(movimentacaoRepository, never()).save(any());
	}

	@Test
	void deveLancar404QuandoProdutoNaoExistir() {
		when(produtoRepository.buscarPorIdComLock(99L)).thenReturn(Optional.empty());
		MovimentacaoRequest request = new MovimentacaoRequest(
				99L, TipoMovimentacao.ENTRADA, 1, null
		);

		assertThrows(RecursoNaoEncontradoException.class, () -> service.registrar(request));
		verify(movimentacaoRepository, never()).save(any());
	}

	@Test
	void deveListarHistoricoDoProduto() {
		Produto produto = produtoComSaldo(10);
		MovimentacaoEstoque movimentacao = MovimentacaoEstoque.builder()
				.id(1L)
				.produto(produto)
				.tipo(TipoMovimentacao.ENTRADA)
				.quantidade(10)
				.dataHora(LocalDateTime.now())
				.motivo("Compra")
				.build();
		when(produtoRepository.existsById(1L)).thenReturn(true);
		when(movimentacaoRepository.findByProdutoIdOrderByDataHoraDesc(1L))
				.thenReturn(List.of(movimentacao));

		List<MovimentacaoResponse> historico = service.listarPorProduto(1L);

		assertEquals(1, historico.size());
		assertEquals(1L, historico.getFirst().produtoId());
	}

	@Test
	void deveLancar404AoConsultarHistoricoDeProdutoInexistente() {
		when(produtoRepository.existsById(99L)).thenReturn(false);

		assertThrows(RecursoNaoEncontradoException.class, () -> service.listarPorProduto(99L));

		verify(movimentacaoRepository, never()).findByProdutoIdOrderByDataHoraDesc(any());
	}

	private MovimentacaoRequest request(TipoMovimentacao tipo, int quantidade) {
		return new MovimentacaoRequest(1L, tipo, quantidade, "  Compra  ");
	}

	private Produto produtoComSaldo(int saldo) {
		return Produto.builder()
				.id(1L)
				.nome("Teclado")
				.sku("TEC-01")
				.quantidadeAtual(saldo)
				.build();
	}
}
