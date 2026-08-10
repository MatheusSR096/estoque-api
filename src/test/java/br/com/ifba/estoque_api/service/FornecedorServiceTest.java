package br.com.ifba.estoque_api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.ifba.estoque_api.dto.FornecedorRequest;
import br.com.ifba.estoque_api.dto.FornecedorResponse;
import br.com.ifba.estoque_api.exception.NegocioException;
import br.com.ifba.estoque_api.exception.RecursoNaoEncontradoException;
import br.com.ifba.estoque_api.model.Fornecedor;
import br.com.ifba.estoque_api.repository.FornecedorRepository;

@ExtendWith(MockitoExtension.class)
public class FornecedorServiceTest {
	
	@Mock
	private FornecedorRepository repository;
	
	@InjectMocks
	private FornecedorService service;
	
	@Test
	public void deveRetornarFornecedorQuandoIdExistir() {
		
		Long id = 1L;
		Fornecedor fornecedorMock = Fornecedor.builder()
				.nomeFantasia("Fornecedor 1")
				.cnpj("83812052000190")
				.telefone("71912345678")
				.email("fornecedor1@gmail.com")
				.endereco("Rua um-dois-tres").build();
		
		when(repository.findById(id)).thenReturn(Optional.of(fornecedorMock));
		
		FornecedorResponse response = service.buscarPorId(id);
		
		assertNotNull(response);
		assertEquals("Fornecedor 1", response.nomeFantasia());
		verify(repository, times(1)).findById(id);
		
	}
	
	@Test
	public void deveRetornarFornecedorQuandoCnpjExistir() {
		
		String cnpj = "83812052000190";
		Fornecedor fornecedorMock = Fornecedor.builder()
				.nomeFantasia("Fornecedor 1")
				.cnpj(cnpj)
				.telefone("71912345678")
				.email("fornecedor1@gmail.com")
				.endereco("Rua um-dois-tres").build();
		
		when(repository.findByCnpj(cnpj)).thenReturn(Optional.of(fornecedorMock));
		
		FornecedorResponse response = service.buscarPorCnpj(cnpj);
		
		assertNotNull(response);
		assertEquals("Fornecedor 1", response.nomeFantasia());
		verify(repository, times(1)).findByCnpj(cnpj);
		
	}
	
	@Test
	public void deveRetornarFornecedorResponseQuandoCriarFornecedor() {
		
		FornecedorRequest request = new FornecedorRequest(
				"Fornecedor 1", 
				"83812052000190", 
				"71912345678", 
				"fornecedor1@email.com", 
				null);
		
		FornecedorResponse responseEsperada = new FornecedorResponse(
				1L, 
				"Fornecedor 1", 
				"83812052000190", 
				"71912345678", 
				"fornecedor1@email.com", 
				null);
		
		Fornecedor fornecedor = Fornecedor.builder()
				.id(1L)
				.nomeFantasia("Fornecedor 1")
				.cnpj("83812052000190")
				.telefone("71912345678")
				.email("fornecedor1@email.com")
				.endereco(null)
				.build();
		
		when(repository.save(any(Fornecedor.class))).thenReturn(fornecedor);
		
		FornecedorResponse response = service.criar(request);
		
		assertNotNull(response);
		assertEquals("Fornecedor 1", response.nomeFantasia());
		assertEquals("83812052000190", response.cnpj());
		assertEquals(responseEsperada, response);
		
		verify(repository, times(1)).save(any(Fornecedor.class));
		
	}
	
	@Test
	public void deveRetornarFornecedorResponseQuandoAtualizarFornecedor() {
		
		Long id = 1L;
		String cnpj = "83812052000190";
		
		Fornecedor fornecedorAntigo = Fornecedor.builder()
				.id(id)
				.nomeFantasia("Fornecedor Antigo")
				.cnpj(cnpj)
				.telefone("72912345678")
				.email("afornecedor@email.com")
				.endereco(null)
				.build();
		
		FornecedorRequest request = new FornecedorRequest(
				"Fornecedor Novo", 
				cnpj, 
				"72912345678", 
				"novoFornecedor@email.com", 
				"Rua do Novo Fornecedor, 838, Bahia");
		
		when(repository.findById(id)).thenReturn(Optional.of(fornecedorAntigo));
		
		when(repository.existsByCnpjAndIdNot(cnpj, id)).thenReturn(false);
		
		when(repository.save(any(Fornecedor.class))).thenAnswer(invocation -> invocation.getArgument(0));
		FornecedorResponse response = service.atualizar(id, request);
		
		assertNotNull(response);
		assertEquals("Fornecedor Novo", response.nomeFantasia());
		assertEquals("novoFornecedor@email.com", response.email());
		assertEquals("Rua do Novo Fornecedor, 838, Bahia", response.endereco());
		
		verify(repository, times(1)).findById(id);
		verify(repository, times(1)).existsByCnpjAndIdNot(cnpj, id);
		verify(repository, times(1)).save(fornecedorAntigo);
		
	}
	
	@Test
	public void deveRetornarVoidQuandoRemoverFornecedor() {
		
		Long id = 1L;
		
		Fornecedor fornecedor = Fornecedor.builder()
				.id(id)
				.build();
		
		when(repository.findById(id)).thenReturn(Optional.of(fornecedor));
		
		service.remover(id);
		
		verify(repository, times(1)).findById(id);
		verify(repository, times(1)).delete(fornecedor);
		
	}

	@Test
	public void deveLancarExcecaoQuandoNaoEncontrarId() {
		
		Long id = 1L;
		
		when(repository.findById(id)).thenReturn(Optional.empty());
		
		RecursoNaoEncontradoException exception = assertThrows(
				RecursoNaoEncontradoException.class,
				() -> service.buscarPorId(id)
		);
		
		assertEquals("Fornecedor não encontrado com ID: " + id, exception.getMessage());
		verify(repository, times(1)).findById(id);
		
	}
	
	@Test
	public void deveLancarExcecaoQuandoNaoEncontrarCnpj() {
		
		String cnpj = "83812052000190";
		
		when(repository.findByCnpj(cnpj)).thenReturn(Optional.empty());
		
		RecursoNaoEncontradoException exception = assertThrows(
				RecursoNaoEncontradoException.class,
				() -> service.buscarPorCnpj(cnpj)
		);
		
		assertEquals("Fornecedor não encontrado com CNPJ: " + cnpj, exception.getMessage());
		verify(repository, times(1)).findByCnpj(cnpj);
		
	}
	
	@Test
	public void deveLancarExcecaoQuandoTentarCriarCnpjNaoUnico() {
		
		String cnpj = "83812052000190";
		
		FornecedorRequest request = new FornecedorRequest("", cnpj, null, null, null);
		
		when(repository.existsByCnpj(cnpj)).thenReturn(true);
		
		NegocioException exception = assertThrows(
				NegocioException.class,
				() -> service.criar(request)
		);
		
		assertEquals("Já existe um fornecedor com o CNPJ: " + cnpj, exception.getMessage());
		verify(repository, times(1)).existsByCnpj(cnpj);
		
	}
	
	@Test
	public void deveLancarExcecaoQuandoTentarAtualizarCnpjNaoUnico() {
		
		String cnpj = "83812052000190";
		Long id = 1L;
		
		Fornecedor fornecedor = Fornecedor.builder()
				.id(id)
				.build();
		FornecedorRequest request = new FornecedorRequest("", cnpj, null, null, null);
		
		when(repository.findById(id)).thenReturn(Optional.of(fornecedor));
		when(repository.existsByCnpjAndIdNot(cnpj, id)).thenReturn(true);
		
		NegocioException exception = assertThrows(
				NegocioException.class,
				() -> service.atualizar(id, request)
		);
		
		assertEquals("Já existe um fornecedor com o CNPJ: " + cnpj, exception.getMessage());
		verify(repository, times(1)).findById(id);
		verify(repository, times(1)).existsByCnpjAndIdNot(cnpj, id);
		
	}
	
	@Test
	public void deveLancarExcecaoQuandoTentarDeletarFornecedorNaoExistente() {
		
		Long id = 1L;
		
		when(repository.findById(id)).thenReturn(Optional.empty());
		
		RecursoNaoEncontradoException exception = assertThrows(
				RecursoNaoEncontradoException.class,
				() -> service.remover(id)
		);
		
		assertEquals("Fornecedor não encontrado com ID: " + id, exception.getMessage());
		verify(repository, times(1)).findById(id);
		
	}
	
}
