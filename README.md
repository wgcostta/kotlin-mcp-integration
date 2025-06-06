# MCP Integration Project

Este é um projeto Spring Boot em Kotlin para integração com um servidor MCP (Model Context Protocol). Ele fornece uma API REST para realizar operações como busca de arquivos e execução de ferramentas no servidor MCP.

## Pré-requisitos
- Java 21
- Maven
- Servidor MCP em execução

## Configuração
1. Clone o repositório:
   ```bash
   git clone <repository-url>
   ```
2. Configure a URL do servidor MCP em `application.properties`:
   ```properties
   mcp.server.url=http://<mcp-server>:<port>
   ```
3. Compile e execute o projeto:
   ```bash
   mvn spring-boot:run
   ```

## Endpoints da API
- **GET /api/mcp/files/search?query={query}**: Busca arquivos no servidor MCP com base no parâmetro `query`.
- **POST /api/mcp/tools/execute**: Executa uma ferramenta no servidor MCP com o corpo da requisição contendo `toolName` e `parameters`.

## Estrutura do Projeto
- `McpIntegrationApplication.kt`: Ponto de entrada da aplicação.
- `MCPConfig.kt`: Configuração do WebClient para comunicação com o servidor MCP.
- `MCPService.kt`: Lógica de negócio para interação com o servidor MCP.
- `MCPController.kt`: Controlador REST com os endpoints da API.
- `FileResponse.kt` e `ToolExecutionRequest.kt`: Modelos de dados para respostas e requisições.

## Dependências
- Spring Boot Web
- WebClient (Spring WebFlux)
- Kotlin
- Jackson para serialização/deserialização JSON

## Testando a API
- Busca de arquivos:
  ```bash
  curl http://localhost:8080/api/mcp/files/search?query=test
  ```
- Execução de ferramenta:
  ```bash
  curl -X POST http://localhost:8080/api/mcp/tools/execute -H "Content-Type: application/json" -d '{"toolName":"exampleTool","parameters":{"param1":"value1"}}'
  ```

## Notas
- Certifique-se de que o servidor MCP está acessível e configurado corretamente.
- Para expandir o projeto, considere adicionar autenticação ou mais endpoints conforme necessário.