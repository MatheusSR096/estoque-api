package br.com.ifba.estoque_api.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "produtos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Produto {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank(message = "O nome do produto é obrigatório")
	@Size(max = 100, message = "O nome deve ter no máximo 100 caracteres")
	@Column(nullable = false, length = 100)
	private String nome;

	@NotBlank(message = "O SKU é obrigatório")
	@Size(max = 50, message = "O SKU deve ter no máximo 50 caracteres")
	@Column(nullable = false, unique = true, length = 50)
	private String sku;

	@Size(max = 255, message = "A descrição deve ter no máximo 255 caracteres")
	@Column(length = 255)
	private String descricao;

	@NotNull(message = "O preço de custo é obrigatório")
	@PositiveOrZero(message = "O preço de custo não pode ser negativo")
	@Column(nullable = false, precision = 12, scale = 2)
	private BigDecimal precoCusto;

	@NotNull(message = "O preço de venda é obrigatório")
	@Positive(message = "O preço de venda deve ser maior que zero")
	@Column(nullable = false, precision = 12, scale = 2)
	private BigDecimal precoVenda;

	// Atualizado pelas movimentações de estoque, nunca diretamente pelo CRUD de produto.
	@NotNull
	@PositiveOrZero(message = "A quantidade atual não pode ser negativa")
	@Column(nullable = false)
	private Integer quantidadeAtual;

	@NotNull(message = "A quantidade mínima é obrigatória")
	@PositiveOrZero(message = "A quantidade mínima não pode ser negativa")
	@Column(nullable = false)
	private Integer quantidadeMinima;

	@NotNull(message = "A categoria é obrigatória")
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "categoria_id", nullable = false)
	private Categoria categoria;

	@NotNull(message = "O fornecedor é obrigatório")
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "fornecedor_id", nullable = false)
	private Fornecedor fornecedor;
}
