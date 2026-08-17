package br.com.ifba.estoque_api.service;

import br.com.ifba.estoque_api.dto.MovimentacaoPorProdutoResponse;
import br.com.ifba.estoque_api.dto.RelatorioMovimentacoesResponse;
import br.com.ifba.estoque_api.dto.RelatorioValorEstoqueResponse;
import br.com.ifba.estoque_api.dto.ValorPorCategoriaResponse;
import br.com.ifba.estoque_api.exception.NegocioException;
import br.com.ifba.estoque_api.model.MovimentacaoEstoque;
import br.com.ifba.estoque_api.model.Produto;
import br.com.ifba.estoque_api.model.TipoMovimentacao;
import br.com.ifba.estoque_api.repository.MovimentacaoEstoqueRepository;
import br.com.ifba.estoque_api.repository.ProdutoRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RelatorioService {

	private static final int PERIODO_PADRAO_DIAS = 30;

	private final ProdutoRepository produtoRepository;
	private final MovimentacaoEstoqueRepository movimentacaoRepository;

	// ---------- Relatório 1: valor de estoque (foto atual, por Produto) ----------

	@Transactional(readOnly = true)
	public RelatorioValorEstoqueResponse gerarRelatorioValorEstoque() {
		List<Produto> produtos = produtoRepository.findAll();

		BigDecimal valorTotalCusto = produtos.stream()
				.map(this::custoTotal)
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		BigDecimal valorTotalVenda = produtos.stream()
				.map(this::vendaTotal)
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		int totalItens = produtos.stream()
				.mapToInt(Produto::getQuantidadeAtual)
				.sum();

		int produtosComEstoqueBaixo = (int) produtos.stream()
				.filter(p -> p.getQuantidadeAtual() <= p.getQuantidadeMinima())
				.count();

		return new RelatorioValorEstoqueResponse(
				produtos.size(),
				totalItens,
				produtosComEstoqueBaixo,
				valorTotalCusto,
				valorTotalVenda,
				valorTotalVenda.subtract(valorTotalCusto),
				agruparValorPorCategoria(produtos)
		);
	}

	private List<ValorPorCategoriaResponse> agruparValorPorCategoria(List<Produto> produtos) {
		Map<Long, List<Produto>> porCategoria = produtos.stream()
				.collect(Collectors.groupingBy(
						p -> p.getCategoria().getId(),
						LinkedHashMap::new,
						Collectors.toList()
				));

		return porCategoria.values().stream()
				.map(this::paraValorPorCategoria)
				.sorted(Comparator.comparing(ValorPorCategoriaResponse::categoriaNome))
				.toList();
	}

	private ValorPorCategoriaResponse paraValorPorCategoria(List<Produto> produtosDaCategoria) {
		Produto primeiro = produtosDaCategoria.get(0);

		BigDecimal custo = produtosDaCategoria.stream()
				.map(this::custoTotal)
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		BigDecimal venda = produtosDaCategoria.stream()
				.map(this::vendaTotal)
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		int itens = produtosDaCategoria.stream()
				.mapToInt(Produto::getQuantidadeAtual)
				.sum();

		return new ValorPorCategoriaResponse(
				primeiro.getCategoria().getId(),
				primeiro.getCategoria().getNome(),
				produtosDaCategoria.size(),
				itens,
				custo,
				venda
		);
	}

	private BigDecimal custoTotal(Produto produto) {
		return produto.getPrecoCusto().multiply(BigDecimal.valueOf(produto.getQuantidadeAtual()));
	}

	private BigDecimal vendaTotal(Produto produto) {
		return produto.getPrecoVenda().multiply(BigDecimal.valueOf(produto.getQuantidadeAtual()));
	}

	// ---------- Relatório 2: movimentações por período (histórico de entradas/saídas) ----------

	@Transactional(readOnly = true)
	public RelatorioMovimentacoesResponse gerarRelatorioMovimentacoes(
			LocalDate dataInicio, LocalDate dataFim, Long produtoId
	) {
		LocalDate inicio = dataInicio != null ? dataInicio : LocalDate.now().minusDays(PERIODO_PADRAO_DIAS);
		LocalDate fim = dataFim != null ? dataFim : LocalDate.now();

		if (inicio.isAfter(fim)) {
			throw new NegocioException("A data inicial não pode ser posterior à data final");
		}

		LocalDateTime inicioDoDia = inicio.atStartOfDay();
		LocalDateTime fimDoDia = fim.atTime(LocalTime.MAX);

		List<MovimentacaoEstoque> movimentacoes =
				movimentacaoRepository.buscarPorPeriodo(inicioDoDia, fimDoDia, produtoId);

		int totalEntradas = somarQuantidade(movimentacoes, TipoMovimentacao.ENTRADA);
		int totalSaidas = somarQuantidade(movimentacoes, TipoMovimentacao.SAIDA);

		BigDecimal valorEntradas = movimentacoes.stream()
				.filter(m -> m.getTipo() == TipoMovimentacao.ENTRADA)
				.map(m -> m.getProduto().getPrecoCusto().multiply(BigDecimal.valueOf(m.getQuantidade())))
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		BigDecimal valorSaidas = movimentacoes.stream()
				.filter(m -> m.getTipo() == TipoMovimentacao.SAIDA)
				.map(m -> m.getProduto().getPrecoVenda().multiply(BigDecimal.valueOf(m.getQuantidade())))
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		return new RelatorioMovimentacoesResponse(
				inicio,
				fim,
				movimentacoes.size(),
				totalEntradas,
				totalSaidas,
				totalEntradas - totalSaidas,
				valorEntradas,
				valorSaidas,
				agruparMovimentacoesPorProduto(movimentacoes)
		);
	}

	private int somarQuantidade(List<MovimentacaoEstoque> movimentacoes, TipoMovimentacao tipo) {
		return movimentacoes.stream()
				.filter(m -> m.getTipo() == tipo)
				.mapToInt(MovimentacaoEstoque::getQuantidade)
				.sum();
	}

	private List<MovimentacaoPorProdutoResponse> agruparMovimentacoesPorProduto(
			List<MovimentacaoEstoque> movimentacoes
	) {
		Map<Long, List<MovimentacaoEstoque>> porProduto = movimentacoes.stream()
				.collect(Collectors.groupingBy(
						m -> m.getProduto().getId(),
						LinkedHashMap::new,
						Collectors.toList()
				));

		return porProduto.values().stream()
				.map(this::paraMovimentacaoPorProduto)
				.sorted(Comparator.comparing(MovimentacaoPorProdutoResponse::produtoNome))
				.toList();
	}

	private MovimentacaoPorProdutoResponse paraMovimentacaoPorProduto(List<MovimentacaoEstoque> movimentacoesDoProduto) {
		Produto produto = movimentacoesDoProduto.get(0).getProduto();
		int entradas = somarQuantidade(movimentacoesDoProduto, TipoMovimentacao.ENTRADA);
		int saidas = somarQuantidade(movimentacoesDoProduto, TipoMovimentacao.SAIDA);

		return new MovimentacaoPorProdutoResponse(
				produto.getId(),
				produto.getNome(),
				produto.getSku(),
				entradas,
				saidas,
				entradas - saidas
		);
	}
}
