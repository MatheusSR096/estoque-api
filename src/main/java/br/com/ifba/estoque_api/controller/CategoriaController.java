package br.com.ifba.estoque_api.controller;

import br.com.ifba.estoque_api.dto.CategoriaRequest;
import br.com.ifba.estoque_api.dto.CategoriaResponse;
import br.com.ifba.estoque_api.service.CategoriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/categorias")
@RequiredArgsConstructor
@Tag(name = "Categorias", description = "CRUD de categorias de produtos")
public class CategoriaController {

	private final CategoriaService categoriaService;

	@GetMapping
	@Operation(summary = "Listar categorias")
	public ResponseEntity<List<CategoriaResponse>> listar() {
		return ResponseEntity.ok(categoriaService.listar());
	}

	@GetMapping("/{id}")
	@Operation(summary = "Buscar categoria por id")
	public ResponseEntity<CategoriaResponse> buscarPorId(@PathVariable Long id) {
		return ResponseEntity.ok(categoriaService.buscarPorId(id));
	}

	@PostMapping
	@Operation(summary = "Criar categoria")
	public ResponseEntity<CategoriaResponse> criar(@Valid @RequestBody CategoriaRequest request) {
		CategoriaResponse criada = categoriaService.criar(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(criada);
	}

	@PutMapping("/{id}")
	@Operation(summary = "Atualizar categoria")
	public ResponseEntity<CategoriaResponse> atualizar(
			@PathVariable Long id,
			@Valid @RequestBody CategoriaRequest request
	) {
		return ResponseEntity.ok(categoriaService.atualizar(id, request));
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "Remover categoria")
	public ResponseEntity<Void> remover(@PathVariable Long id) {
		categoriaService.remover(id);
		return ResponseEntity.noContent().build();
	}
}
