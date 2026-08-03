# Estoque API

API REST para gerenciamento de estoque, desenvolvida com Spring Boot, Spring
Web MVC, Spring Data JPA, Bean Validation e MySQL.

## Tecnologias e versões

- Java 21
- Spring Boot 4.1.0
- Apache Maven 3.9.16 (incluído por meio do Maven Wrapper)
- MySQL
- Lombok

## Pré-requisitos

Antes de executar o projeto, instale:

- JDK 21
- MySQL em execução

Não é necessário instalar o Maven: o projeto inclui o Maven Wrapper (`mvnw` e
`mvnw.cmd`), que baixa e utiliza a versão correta automaticamente.

## Instalação

1. Clone o repositório:

   ```bash
   git clone https://github.com/MatheusSR096/estoque-api.git
   cd estoque-api
   ```

2. Crie um banco de dados no MySQL:

   ```sql
   CREATE DATABASE estoque;
   ```

3. Configure a conexão com o banco usando variáveis de ambiente.

   Linux/macOS:

   ```bash
   export SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/estoque
   export SPRING_DATASOURCE_USERNAME=root
   export SPRING_DATASOURCE_PASSWORD=sua_senha
   ```

   Windows (PowerShell):

   ```powershell
   $env:SPRING_DATASOURCE_URL="jdbc:mysql://localhost:3306/estoque"
   $env:SPRING_DATASOURCE_USERNAME="root"
   $env:SPRING_DATASOURCE_PASSWORD="sua_senha"
   ```

4. Execute a aplicação.

   Linux/macOS:

   ```bash
   ./mvnw spring-boot:run
   ```

   Windows:

   ```powershell
   .\mvnw.cmd spring-boot:run
   ```

A aplicação ficará disponível em `http://localhost:8080`.

## Testes

Linux/macOS:

```bash
./mvnw test
```

Windows:

```powershell
.\mvnw.cmd test
```

## Gerar o arquivo executável

Linux/macOS:

```bash
./mvnw clean package
java -jar target/estoque-api-0.0.1-SNAPSHOT.jar
```

No Windows, substitua `./mvnw` por `.\mvnw.cmd`.

## Estrutura inicial

```text
src/
├── main/
│   ├── java/br/com/ifba/estoque_api/
│   └── resources/application.properties
└── test/
    └── java/br/com/ifba/estoque_api/
```

## Fluxo de trabalho em equipe

Cada pessoa deve desenvolver sua funcionalidade em uma branch própria. Não faça
commits diretamente na branch `main`.

1. Antes de começar, acesse o projeto e atualize a `main`:

   ```bash
   git switch main
   git pull origin main
   ```

2. Crie uma branch a partir da `main` atualizada:

   ```bash
   git switch -c feature/nome-da-funcionalidade
   ```

   Use nomes descritivos, por exemplo:

   - `feature/cadastro-produto` para uma nova funcionalidade;
   - `fix/correcao-validacao-produto` para uma correção;
   - `docs/atualizacao-readme` para documentação.

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

## Boas práticas de configuração

Não adicione senhas ou outras credenciais ao repositório. Em desenvolvimento,
prefira variáveis de ambiente; em produção, use o gerenciador de segredos da
plataforma de hospedagem.
