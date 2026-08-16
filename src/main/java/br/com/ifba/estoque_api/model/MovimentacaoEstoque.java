package br.com.ifba.estoque_api.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
		name = "movimentacoes_estoque",
		indexes = @Index(name = "idx_movimentacao_produto_data", columnList = "produto_id,data_hora")
)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovimentacaoEstoque {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull(message = "O produto é obrigatório")
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "produto_id", nullable = false)
	private Produto produto;

	@NotNull(message = "O tipo da movimentação é obrigatório")
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 10)
	private TipoMovimentacao tipo;

	@NotNull(message = "A quantidade é obrigatória")
	@Positive(message = "A quantidade deve ser maior que zero")
	@Column(nullable = false)
	private Integer quantidade;

	@Column(name = "data_hora", nullable = false, updatable = false)
	private LocalDateTime dataHora;

	@Size(max = 255, message = "O motivo deve ter no máximo 255 caracteres")
	@Column(length = 255)
	private String motivo;

	@PrePersist
	private void preencherDataHora() {
		if (dataHora == null) {
			dataHora = LocalDateTime.now();
		}
	}
}
