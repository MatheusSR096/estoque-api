package br.com.ifba.estoque_api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

	@Bean
	public OpenAPI estoqueOpenAPI() {
		return new OpenAPI()
				.info(new Info()
						.title("Estoque API")
						.description("API REST para gerenciamento de estoque de produtos")
						.version("0.0.1")
						.contact(new Contact()
								.name("IFBA — Desenvolvimento de Sistemas")
						)
				);
	}
}
