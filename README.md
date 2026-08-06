# Estoque API

API REST para gerenciamento de estoque de produtos (loja, depósito ou distribuidora),
desenvolvida com Spring Boot em arquitetura em camadas.

O sistema permite cadastro de produtos, categorias e fornecedores, controle de
entradas e saídas de estoque, alertas de estoque baixo e relatórios simples.

## Tecnologias e versões

- Java 21
- Spring Boot 4.1.0
- Spring Web MVC · Spring Data JPA · Bean Validation
- Apache Maven 3.9.16 (incluído por meio do Maven Wrapper)
- MySQL
- Lombok
- springdoc-openapi (Swagger UI)

## Pré-requisitos

Antes de executar o projeto, instale:

- **JDK 21** (obrigatório — o projeto não compila com Java 17)
- MySQL em execução

Não é necessário instalar o Maven: o projeto inclui o Maven Wrapper (`mvnw` e
`mvnw.cmd`), que baixa e utiliza a versão correta automaticamente.

## Instalação

1. Clone o repositório:

   ```bash
   git clone https://github.com/MatheusSR096/estoque-api.git
   cd estoque-api
   ```

2. Suba o MySQL local com Docker (recomendado):

   ```bash
   docker compose up -d
   ```

   Isso cria o banco `estoque` na porta **3307**, com usuário `root` e senha `root`.

   Alternativa sem Docker: crie o banco no MySQL instalado na máquina:

   ```sql
   CREATE DATABASE estoque;
   ```

3. Configure a conexão com o banco usando variáveis de ambiente.

   Com Docker Compose (porta 3307):

   Linux/macOS:

   ```bash
   export DB_URL=jdbc:mysql://localhost:3307/estoque
   export DB_USERNAME=root
   export DB_PASSWORD=root
   ```

   Windows (PowerShell):

   ```powershell
   $env:DB_URL="jdbc:mysql://localhost:3307/estoque"
   $env:DB_USERNAME="root"
   $env:DB_PASSWORD="root"
   ```

   Com MySQL instalado localmente (porta 3306):

   ```powershell
   $env:DB_URL="jdbc:mysql://localhost:3306/estoque"
   $env:DB_USERNAME="root"
   $env:DB_PASSWORD="sua_senha"
   ```

   Se as variáveis não forem definidas, a aplicação usa os valores padrão:
   `jdbc:mysql://localhost:3306/estoque`, usuário `root` e senha vazia.

4. Execute a aplicação (requer **JDK 21** no `PATH` ou em `JAVA_HOME`).

   Linux/macOS:

   ```bash
   ./mvnw spring-boot:run
   ```

   Windows:

   ```powershell
   $env:JAVA_HOME="C:\Program Files\Eclipse Adoptium\jdk-21.0.12.8-hotspot"
   $env:Path="$env:JAVA_HOME\bin;$env:Path"
   .\mvnw.cmd spring-boot:run
   ```

A aplicação ficará disponível em `http://localhost:8080`.

Documentação interativa (Swagger UI): `http://localhost:8080/swagger-ui.html`

## Endpoints disponíveis

### Categorias

| Método | Rota | Descrição | Status de sucesso |
|--------|------|-----------|-------------------|
| GET | `/api/categorias` | Listar categorias | 200 |
| GET | `/api/categorias/{id}` | Buscar por id | 200 |
| POST | `/api/categorias` | Criar categoria | 201 |
| PUT | `/api/categorias/{id}` | Atualizar categoria | 200 |
| DELETE | `/api/categorias/{id}` | Remover categoria | 204 |

Exemplo de criação:

```http
POST /api/categorias
Content-Type: application/json

{
  "nome": "Eletrônicos",
  "descricao": "Produtos eletrônicos em geral"
}
```

### Em desenvolvimento (próximos módulos)

| Recurso | Rotas previstas |
|---------|-----------------|
| Fornecedores | `/api/fornecedores` |
| Produtos | `/api/produtos`, `/api/produtos/estoque-baixo` |
| Movimentações | `/api/movimentacoes`, `/api/movimentacoes/produto/{produtoId}` |
| Relatórios | `/api/relatorios/valor-estoque` |

## Tratamento de erros

Erros são retornados em formato padronizado:

```json
{
  "timestamp": "2026-08-05T13:00:00",
  "status": 400,
  "mensagem": "Erro de validação",
  "campos": {
    "nome": "O nome da categoria é obrigatório"
  }
}
```

| Situação | HTTP |
|----------|------|
| Validação de campos | 400 |
| Recurso não encontrado | 404 |
| Regra de negócio (ex.: nome duplicado) | 409 |
| Erro interno | 500 |

## Testes

Linux/macOS:

```bash
./mvnw test
```

Windows:

```powershell
.\mvnw.cmd test
```

> Os testes de contexto (`@SpringBootTest`) exigem MySQL acessível com as
> mesmas variáveis de ambiente usadas na execução.

## Gerar o arquivo executável

Linux/macOS:

```bash
./mvnw clean package
java -jar target/estoque-api-0.0.1-SNAPSHOT.jar
```

No Windows, substitua `./mvnw` por `.\mvnw.cmd`.

## Estrutura do projeto

```text
src/main/java/br/com/ifba/estoque_api/
├── EstoqueApiApplication.java
├── config/          → CORS, OpenAPI/Swagger
├── controller/      → endpoints REST (recebe/retorna DTOs)
├── dto/             → Request/Response e ErroResponse (records)
├── exception/       → exceções customizadas + @RestControllerAdvice
├── model/           → entidades JPA
├── repository/      → interfaces Spring Data JPA
└── service/         → regras de negócio

src/main/resources/
└── application.properties
```

Fluxo padrão: `Controller` recebe DTO validado → `Service` aplica regra de
negócio e usa `Repository` → retorna DTO de resposta (entidades JPA nunca são
expostas diretamente).

## Fluxo de trabalho em equipe

Cada pessoa deve desenvolver sua funcionalidade em uma branch própria. Não faça
commits diretamente na branch `main`.

Sugestão de branches por módulo:

- `feature/categoria`
- `feature/fornecedor`
- `feature/produto`
- `feature/movimentacao`
- `feature/relatorios`

1. Antes de começar, acesse o projeto e atualize a `main`:

   ```bash
   git switch main
   git pull origin main
   ```

2. Crie uma branch a partir da `main` atualizada:

   ```bash
   git switch -c feature/nome-da-funcionalidade
   ```

3. Desenvolva e teste sua alteração. Depois, crie o commit:

   ```bash
   git add .
   git commit -m "feat: adiciona cadastro de produto"
   ```

4. Envie sua branch ao GitHub:

   ```bash
   git push -u origin feature/nome-da-funcionalidade
   ```

5. No GitHub, abra um **Pull Request** da sua branch para a branch `main`.
   Descreva o que foi alterado e aguarde a revisão antes de fazer o merge.

6. Depois que o Pull Request for aprovado e integrado, atualize sua `main` local
   antes de iniciar outra funcionalidade:

   ```bash
   git switch main
   git pull origin main
   ```
