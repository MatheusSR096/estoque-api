package br.com.ifba.estoque_api.controller;

import br.com.ifba.estoque_api.dto.RelatorioMovimentacoesResponse;
import br.com.ifba.estoque_api.dto.RelatorioValorEstoqueResponse;
import br.com.ifba.estoque_api.service.RelatorioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/relatorios")
@RequiredArgsConstructor
@Tag(name = "Relatórios", description = "Relatórios agregados sobre estoque e movimentações")
public class RelatorioController {

	private final RelatorioService relatorioService;

	@GetMapping("/valor-estoque")
	@Operation(summary = "Valor total investido/potencial de venda do estoque atual, geral e por categoria")
	public ResponseEntity<RelatorioValorEstoqueResponse> valorEstoque() {
		return ResponseEntity.ok(relatorioService.gerarRelatorioValorEstoque());
	}

	@GetMapping("/movimentacoes")
	@Operation(summary = "Resumo de entradas e saídas em um período (padrão: últimos 30 dias), "
			+ "opcionalmente filtrado por produto")
	public ResponseEntity<RelatorioMovimentacoesResponse> movimentacoes(
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
			@RequestParam(required = false) Long produtoId
	) {
		return ResponseEntity.ok(relatorioService.gerarRelatorioMovimentacoes(dataInicio, dataFim, produtoId));
	}
}
