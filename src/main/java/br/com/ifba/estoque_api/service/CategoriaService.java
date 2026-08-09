package br.com.ifba.estoque_api.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.ifba.estoque_api.dto.CategoriaRequest;
import br.com.ifba.estoque_api.dto.CategoriaResponse;
import br.com.ifba.estoque_api.exception.NegocioException;
import br.com.ifba.estoque_api.exception.RecursoNaoEncontradoException;
import br.com.ifba.estoque_api.model.Categoria;
import br.com.ifba.estoque_api.repository.CategoriaRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoriaService {

	private final CategoriaRepository categoriaRepository;

	@Transactional(readOnly = true)
	public List<CategoriaResponse> listar() {
		return categoriaRepository.findAll().stream()
				.map(CategoriaResponse::fromEntity)
				.toList();
	}

	@Transactional(readOnly = true)
	public CategoriaResponse buscarPorId(Long id) {
		return CategoriaResponse.fromEntity(buscarEntidadePorId(id));
	}

	@Transactional
	public CategoriaResponse criar(CategoriaRequest request) {
		validarNomeUnico(request.nome(), null);

		Categoria categoria = Categoria.builder()
				.nome(request.nome().trim())
				.descricao(normalizarDescricao(request.descricao()))
				.build();

		return CategoriaResponse.fromEntity(categoriaRepository.save(categoria));
	}

	@Transactional
	public CategoriaResponse atualizar(Long id, CategoriaRequest request) {
		Categoria categoria = buscarEntidadePorId(id);
		validarNomeUnico(request.nome(), id);

		categoria.setNome(request.nome().trim());
		categoria.setDescricao(normalizarDescricao(request.descricao()));

		return CategoriaResponse.fromEntity(categoriaRepository.save(categoria));
	}

	@Transactional
	public void remover(Long id) {
		Categoria categoria = buscarEntidadePorId(id);
		categoriaRepository.delete(categoria);
	}

	@Transactional(readOnly = true)
	public Categoria buscarEntidadePorId(Long id) {
		return categoriaRepository.findById(id)
				.orElseThrow(() -> new RecursoNaoEncontradoException(
						"Categoria não encontrada com id: " + id
				));
	}

	private void validarNomeUnico(String nome, Long idAtual) {
		String nomeNormalizado = nome.trim();
		boolean existe = idAtual == null
				? categoriaRepository.existsByNomeIgnoreCase(nomeNormalizado)
				: categoriaRepository.existsByNomeIgnoreCaseAndIdNot(nomeNormalizado, idAtual);

		if (existe) {
			throw new NegocioException("Já existe uma categoria com o nome: " + nomeNormalizado);
		}
	}

	private String normalizarDescricao(String descricao) {
		if (descricao == null || descricao.isBlank()) {
			return null;
		}
		return descricao.trim();
	}
}
