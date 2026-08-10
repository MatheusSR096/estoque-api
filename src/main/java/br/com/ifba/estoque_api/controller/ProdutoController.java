package br.com.ifba.estoque_api.controller;

import br.com.ifba.estoque_api.dto.ProdutoRequest;
import br.com.ifba.estoque_api.dto.ProdutoResponse;
import br.com.ifba.estoque_api.service.ProdutoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/produtos")
@RequiredArgsConstructor
@Tag(name = "Produtos", description = "CRUD de produtos e consulta de estoque baixo")
public class ProdutoController {

	private final ProdutoService produtoService;

	@GetMapping
	@Operation(summary = "Listar produtos com paginação e filtros por nome, categoria e fornecedor")
	public ResponseEntity<Page<ProdutoResponse>> listar(
			@RequestParam(required = false) String nome,
			@RequestParam(required = false) Long categoriaId,
			@RequestParam(required = false) Long fornecedorId,
			@PageableDefault(size = 20, sort = "nome", direction = Sort.Direction.ASC) Pageable pageable
	) {
		return ResponseEntity.ok(produtoService.listar(nome, categoriaId, fornecedorId, pageable));
	}

	@GetMapping("/estoque-baixo")
	@Operation(summary = "Listar produtos com quantidade atual menor ou igual à mínima")
	public ResponseEntity<List<ProdutoResponse>> listarEstoqueBaixo() {
		return ResponseEntity.ok(produtoService.listarComEstoqueBaixo());
	}

	@GetMapping("/{id}")
	@Operation(summary = "Buscar produto por id")
	public ResponseEntity<ProdutoResponse> buscarPorId(@PathVariable Long id) {
		return ResponseEntity.ok(produtoService.buscarPorId(id));
	}

	@PostMapping
	@Operation(summary = "Criar produto")
	public ResponseEntity<ProdutoResponse> criar(@Valid @RequestBody ProdutoRequest request) {
		ProdutoResponse criado = produtoService.criar(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(criado);
	}

	@PutMapping("/{id}")
	@Operation(summary = "Atualizar produto (a quantidade atual só muda por movimentação)")
	public ResponseEntity<ProdutoResponse> atualizar(
			@PathVariable Long id,
			@Valid @RequestBody ProdutoRequest request
	) {
		return ResponseEntity.ok(produtoService.atualizar(id, request));
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "Remover produto")
	public ResponseEntity<Void> remover(@PathVariable Long id) {
		produtoService.remover(id);
		return ResponseEntity.noContent().build();
	}
}
