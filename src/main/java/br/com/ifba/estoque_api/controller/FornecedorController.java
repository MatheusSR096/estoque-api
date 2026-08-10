package br.com.ifba.estoque_api.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.ifba.estoque_api.dto.FornecedorRequest;
import br.com.ifba.estoque_api.dto.FornecedorResponse;
import br.com.ifba.estoque_api.dto.validation.ValidCnpj;
import br.com.ifba.estoque_api.service.FornecedorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/fornecedores")
@Validated
@Tag(name = "Fornecedores", description = "CRUD de fornecedores")
@RequiredArgsConstructor
public class FornecedorController {
	
	private final FornecedorService service;
	
	@GetMapping
	@Operation(summary = "Listar fornecedores")
	public ResponseEntity<List<FornecedorResponse>> listar() {
		
		return ResponseEntity.ok(service.listar());
		
	}
	
	@GetMapping("/cnpj/{cnpj}")
	@Operation(summary = "Buscar fornecedor por CNPJ")
	public ResponseEntity<FornecedorResponse> buscarPorCnpj(
			@PathVariable 
			@NotBlank(message = "O CNPJ não pode estar vazio") 
			@ValidCnpj String cnpj) {
		
		return ResponseEntity.ok(service.buscarPorCnpj(cnpj));
		
	}
	
	@GetMapping("/{id}")
	@Operation(summary = "Buscar fornecedor por ID")
	public ResponseEntity<FornecedorResponse> buscarPorId(@PathVariable Long id) {
		
		return ResponseEntity.ok(service.buscarPorId(id));
		
	}
	
	@PostMapping
	@Operation(summary = "Criar fornecedor")
	public ResponseEntity<FornecedorResponse> criar(
			@Valid 
			@RequestBody FornecedorRequest request) {
		
		FornecedorResponse criada = service.criar(request);
		
		return ResponseEntity.status(HttpStatus.CREATED).body(criada);
		
	}
	
	@PutMapping("/{id}")
	@Operation(summary = "Atualizar fornecedor")
	public ResponseEntity<FornecedorResponse> atualizar(
			@PathVariable Long id, 
			@Valid 
			@RequestBody FornecedorRequest request) {
		
		return ResponseEntity.ok(service.atualizar(id, request));
		
	}
	
	@DeleteMapping("/{id}")
	@Operation(summary = "Remover fornecedor")
	public ResponseEntity<Void> remover(@PathVariable Long id) {
		
		service.remover(id);
		
		return ResponseEntity.noContent().build();
		
	}

}
