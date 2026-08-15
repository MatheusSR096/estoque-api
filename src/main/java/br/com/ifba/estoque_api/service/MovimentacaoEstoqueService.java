package br.com.ifba.estoque_api.service;

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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MovimentacaoEstoqueService {

	private final MovimentacaoEstoqueRepository movimentacaoRepository;
	private final ProdutoRepository produtoRepository;

	@Transactional(readOnly = true)
	public List<MovimentacaoResponse> listar() {
		return movimentacaoRepository.findAllByOrderByDataHoraDesc().stream()
				.map(MovimentacaoResponse::fromEntity)
				.toList();
	}

	@Transactional(readOnly = true)
	public List<MovimentacaoResponse> listarPorProduto(Long produtoId) {
		if (!produtoRepository.existsById(produtoId)) {
			throw produtoNaoEncontrado(produtoId);
		}

		return movimentacaoRepository.findByProdutoIdOrderByDataHoraDesc(produtoId).stream()
				.map(MovimentacaoResponse::fromEntity)
				.toList();
	}

	@Transactional
	public MovimentacaoResponse registrar(MovimentacaoRequest request) {
		validarQuantidade(request.quantidade());

		Produto produto = produtoRepository.buscarPorIdComLock(request.produtoId())
				.orElseThrow(() -> produtoNaoEncontrado(request.produtoId()));

		int novoSaldo = calcularNovoSaldo(produto, request.tipo(), request.quantidade());
		produto.setQuantidadeAtual(novoSaldo);
		produtoRepository.save(produto);

		MovimentacaoEstoque movimentacao = MovimentacaoEstoque.builder()
				.produto(produto)
				.tipo(request.tipo())
				.quantidade(request.quantidade())
				.dataHora(LocalDateTime.now())
				.motivo(normalizarMotivo(request.motivo()))
				.build();

		return MovimentacaoResponse.fromEntity(movimentacaoRepository.save(movimentacao));
	}

	private int calcularNovoSaldo(Produto produto, TipoMovimentacao tipo, int quantidade) {
		if (tipo == null) {
			throw new NegocioException("O tipo da movimentação é obrigatório");
		}

		if (tipo == TipoMovimentacao.SAIDA) {
			if (quantidade > produto.getQuantidadeAtual()) {
				throw new EstoqueInsuficienteException(
						"Estoque insuficiente para o produto " + produto.getNome()
								+ ": saldo atual " + produto.getQuantidadeAtual()
								+ ", saída solicitada " + quantidade
				);
			}
			return produto.getQuantidadeAtual() - quantidade;
		}

		try {
			return Math.addExact(produto.getQuantidadeAtual(), quantidade);
		} catch (ArithmeticException ex) {
			throw new NegocioException("A entrada excede a quantidade máxima suportada");
		}
	}

	private void validarQuantidade(Integer quantidade) {
		if (quantidade == null || quantidade <= 0) {
			throw new NegocioException("A quantidade deve ser maior que zero");
		}
	}

	private String normalizarMotivo(String motivo) {
		if (motivo == null || motivo.isBlank()) {
			return null;
		}
		return motivo.trim();
	}

	private RecursoNaoEncontradoException produtoNaoEncontrado(Long produtoId) {
		return new RecursoNaoEncontradoException("Produto não encontrado com id: " + produtoId);
	}
}
