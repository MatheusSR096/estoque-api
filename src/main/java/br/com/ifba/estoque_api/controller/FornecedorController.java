package br.com.ifba.estoque_api.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.ifba.estoque_api.dto.FornecedorRequest;
import br.com.ifba.estoque_api.dto.FornecedorResponse;
import br.com.ifba.estoque_api.service.FornecedorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/fornecedores")
@RequiredArgsConstructor
@Tag(name = "Fornecedores", description = "CRUD de fornecedores")
public class FornecedorController {
	
	private final FornecedorService service;
	
	@GetMapping
	@Operation(summary = "Listar fornecedores")
	public ResponseEntity<List<FornecedorResponse>> listar() {
		
		return ResponseEntity.ok(service.listar());
		
	}
	
	@PostMapping
	@Operation(summary = "Criar fornecedor")
	public ResponseEntity<FornecedorResponse> criar(@Valid @RequestBody FornecedorRequest request) {
		
		FornecedorResponse criada = service.criar(request);
		
		return ResponseEntity.status(HttpStatus.CREATED).body(criada);
		
	}

}
