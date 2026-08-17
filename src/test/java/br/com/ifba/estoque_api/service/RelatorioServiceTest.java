package br.com.ifba.estoque_api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;

import br.com.ifba.estoque_api.dto.MovimentacaoPorProdutoResponse;
import br.com.ifba.estoque_api.dto.RelatorioMovimentacoesResponse;
import br.com.ifba.estoque_api.dto.RelatorioValorEstoqueResponse;
import br.com.ifba.estoque_api.dto.ValorPorCategoriaResponse;
import br.com.ifba.estoque_api.exception.NegocioException;
import br.com.ifba.estoque_api.model.Categoria;
import br.com.ifba.estoque_api.model.Fornecedor;
import br.com.ifba.estoque_api.model.MovimentacaoEstoque;
import br.com.ifba.estoque_api.model.Produto;
import br.com.ifba.estoque_api.model.TipoMovimentacao;
import br.com.ifba.estoque_api.repository.MovimentacaoEstoqueRepository;
import br.com.ifba.estoque_api.repository.ProdutoRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class RelatorioServiceTest {

	@Mock
	private ProdutoRepository produtoRepository;

	@Mock
	private MovimentacaoEstoqueRepository movimentacaoRepository;

	@InjectMocks
	private RelatorioService service;

	private final Fornecedor fornecedor = Fornecedor.builder()
			.id(1L).nomeFantasia("Acme").cnpj("12345678000199").build();

	private Produto produto(Long id, Long categoriaId, String categoriaNome, String custo, String venda, int atual, int minima) {
		return Produto.builder()
				.id(id)
				.nome("Produto " + id)
				.sku("SKU-" + id)
				.precoCusto(new BigDecimal(custo))
				.precoVenda(new BigDecimal(venda))
				.quantidadeAtual(atual)
				.quantidadeMinima(minima)
				.categoria(Categoria.builder().id(categoriaId).nome(categoriaNome).build())
				.fornecedor(fornecedor)
				.build();
	}

	private MovimentacaoEstoque movimentacao(Produto produto, TipoMovimentacao tipo, int quantidade) {
		return MovimentacaoEstoque.builder()
				.id(1L)
				.produto(produto)
				.tipo(tipo)
				.quantidade(quantidade)
				.dataHora(LocalDateTime.now())
				.build();
	}

	// ---------- Relatório de valor de estoque ----------

	@Test
	public void deveCalcularTotaisGeraisDoEstoque() {
		when(produtoRepository.findAll()).thenReturn(List.of(
				produto(1L, 1L, "Periféricos", "10.00", "25.00", 5, 2),
				produto(2L, 2L, "Eletrônicos", "100.00", "150.00", 1, 3)
		));

		RelatorioValorEstoqueResponse relatorio = service.gerarRelatorioValorEstoque();

		assertEquals(2, relatorio.totalProdutos());
		assertEquals(6, relatorio.totalItensEmEstoque());
		assertEquals(1, relatorio.produtosComEstoqueBaixo());
		assertEquals(new BigDecimal("150.00"), relatorio.valorTotalCusto());
		assertEquals(new BigDecimal("275.00"), relatorio.valorTotalVenda());
		assertEquals(new BigDecimal("125.00"), relatorio.lucroPotencial());
	}

	@Test
	public void deveAgruparValoresPorCategoriaOrdenadoPorNome() {
		when(produtoRepository.findAll()).thenReturn(List.of(
				produto(1L, 2L, "Periféricos", "10.00", "25.00", 5, 2),
				produto(2L, 1L, "Eletrônicos", "100.00", "150.00", 2, 1)
		));

		List<ValorPorCategoriaResponse> porCategoria = service.gerarRelatorioValorEstoque().porCategoria();

		assertEquals(2, porCategoria.size());
		assertEquals("Eletrônicos", porCategoria.get(0).categoriaNome());
		assertEquals("Periféricos", porCategoria.get(1).categoriaNome());
		assertEquals(new BigDecimal("200.00"), porCategoria.get(0).valorCusto());
	}

	@Test
	public void deveRetornarZeradoQuandoNaoHaProdutos() {
		when(produtoRepository.findAll()).thenReturn(List.of());

		RelatorioValorEstoqueResponse relatorio = service.gerarRelatorioValorEstoque();

		assertEquals(0, relatorio.totalProdutos());
		assertEquals(BigDecimal.ZERO, relatorio.valorTotalCusto());
		assertEquals(BigDecimal.ZERO, relatorio.lucroPotencial());
		assertEquals(0, relatorio.porCategoria().size());
	}

	// ---------- Relatório de movimentações por período ----------

	@Test
	public void deveCalcularTotaisDeEntradasESaidasDoPeriodo() {
		Produto produto1 = produto(1L, 1L, "Periféricos", "10.00", "25.00", 20, 2);
		Produto produto2 = produto(2L, 1L, "Periféricos", "5.00", "12.00", 8, 2);

		when(movimentacaoRepository.buscarPorPeriodo(any(), any(), isNull())).thenReturn(List.of(
				movimentacao(produto1, TipoMovimentacao.ENTRADA, 10),
				movimentacao(produto1, TipoMovimentacao.SAIDA, 3),
				movimentacao(produto2, TipoMovimentacao.ENTRADA, 4)
		));

		RelatorioMovimentacoesResponse relatorio =
				service.gerarRelatorioMovimentacoes(LocalDate.now().minusDays(7), LocalDate.now(), null);

		assertEquals(3, relatorio.totalMovimentacoes());
		assertEquals(14, relatorio.totalEntradas());
		assertEquals(3, relatorio.totalSaidas());
		assertEquals(11, relatorio.saldoPeriodo());
		assertEquals(new BigDecimal("120.00"), relatorio.valorEntradas());
		assertEquals(new BigDecimal("75.00"), relatorio.valorSaidas());
	}

	@Test
	public void deveAgruparMovimentacoesPorProduto() {
		Produto produto1 = produto(1L, 1L, "Periféricos", "10.00", "25.00", 20, 2);

		when(movimentacaoRepository.buscarPorPeriodo(any(), any(), eq(1L))).thenReturn(List.of(
				movimentacao(produto1, TipoMovimentacao.ENTRADA, 10),
				movimentacao(produto1, TipoMovimentacao.SAIDA, 3)
		));

		List<MovimentacaoPorProdutoResponse> porProduto =
				service.gerarRelatorioMovimentacoes(null, null, 1L).porProduto();

		assertEquals(1, porProduto.size());
		assertEquals(10, porProduto.get(0).totalEntradas());
		assertEquals(3, porProduto.get(0).totalSaidas());
		assertEquals(7, porProduto.get(0).saldoPeriodo());
	}

	@Test
	public void deveUsarUltimosTrintaDiasQuandoDatasNaoInformadas() {
		when(movimentacaoRepository.buscarPorPeriodo(any(), any(), isNull())).thenReturn(List.of());

		RelatorioMovimentacoesResponse relatorio = service.gerarRelatorioMovimentacoes(null, null, null);

		assertEquals(LocalDate.now().minusDays(30), relatorio.dataInicio());
		assertEquals(LocalDate.now(), relatorio.dataFim());
	}

	@Test
	public void deveRejeitarDataInicialPosteriorADataFinal() {
		LocalDate inicio = LocalDate.now();
		LocalDate fim = LocalDate.now().minusDays(1);

		assertThrows(NegocioException.class, () -> service.gerarRelatorioMovimentacoes(inicio, fim, null));
	}
}
