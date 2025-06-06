package com.example.mcpintegration.controller

import com.example.mcpintegration.model.FileResponse
import com.example.mcpintegration.model.ToolExecutionRequest
import com.example.mcpintegration.service.MCPService
import io.mockk.mockk
import io.mockk.every
import org.junit.jupiter.api.Test
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Mono
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MCPControllerTest {

    private val mcpService: MCPService = mockk()
    private val controller = MCPController(mcpService)
    private val webTestClient = WebTestClient.bindToController(controller).build()

    @Test
    fun `searchFiles should return list of files when successful`() {
        val query = "test"
        val fileResponse : List<FileResponse> = listOf(
            FileResponse("test.txt", "/path/test.txt", 1024, LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
        )

        every { mcpService.searchFiles(query) } returns Mono.just(fileResponse)

        webTestClient.get()
            .uri("/api/mcp/files/search?query=$query")
            .exchange()
            .expectStatus().isOk
            .expectBodyList(FileResponse::class.java)
    }

    @Test
    fun `searchFiles should return 500 when service fails`() {
        val query = "test"
        every { mcpService.searchFiles(query) } returns Mono.error(RuntimeException("Service error"))

        webTestClient.get()
            .uri("/api/mcp/files/search?query=$query")
            .exchange()
            .expectStatus().is5xxServerError
    }

    @Test
    fun `executeTool should return result when successful`() {
        val toolRequest = ToolExecutionRequest("exampleTool", mapOf("param1" to "value1"))
        val responseBody = "Tool executed successfully"

        every { mcpService.executeTool(toolRequest) } returns Mono.just(responseBody)

        webTestClient.post()
            .uri("/api/mcp/tools/execute")
            .bodyValue(toolRequest)
            .exchange()
            .expectStatus().isOk
            .expectBody(String::class.java)
            .isEqualTo(responseBody)
    }

    @Test
    fun `executeTool should return 500 when service fails`() {
        val toolRequest = ToolExecutionRequest("exampleTool", mapOf("param1" to "value1"))
        every { mcpService.executeTool(toolRequest) } returns Mono.error(RuntimeException("Service error"))

        webTestClient.post()
            .uri("/api/mcp/tools/execute")
            .bodyValue(toolRequest)
            .exchange()
            .expectStatus().is5xxServerError
    }
}