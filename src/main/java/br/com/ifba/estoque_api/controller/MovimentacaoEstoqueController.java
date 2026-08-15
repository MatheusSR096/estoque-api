package br.com.ifba.estoque_api.controller;

import br.com.ifba.estoque_api.dto.MovimentacaoRequest;
import br.com.ifba.estoque_api.dto.MovimentacaoResponse;
import br.com.ifba.estoque_api.service.MovimentacaoEstoqueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/movimentacoes")
@RequiredArgsConstructor
@Validated
@Tag(name = "Movimentações de estoque", description = "Entradas, saídas e histórico do estoque")
public class MovimentacaoEstoqueController {

	private final MovimentacaoEstoqueService movimentacaoService;

	@GetMapping
	@Operation(summary = "Listar todas as movimentações, das mais recentes para as mais antigas")
	public ResponseEntity<List<MovimentacaoResponse>> listar() {
		return ResponseEntity.ok(movimentacaoService.listar());
	}

	@GetMapping("/produto/{produtoId}")
	@Operation(summary = "Listar o histórico de movimentações de um produto")
	public ResponseEntity<List<MovimentacaoResponse>> listarPorProduto(
			@PathVariable
			@Positive(message = "O id do produto deve ser maior que zero")
			Long produtoId
	) {
		return ResponseEntity.ok(movimentacaoService.listarPorProduto(produtoId));
	}

	@PostMapping
	@Operation(summary = "Registrar uma entrada ou saída e atualizar o saldo do produto")
	public ResponseEntity<MovimentacaoResponse> registrar(
			@Valid @RequestBody MovimentacaoRequest request
	) {
		MovimentacaoResponse registrada = movimentacaoService.registrar(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(registrada);
	}
}
