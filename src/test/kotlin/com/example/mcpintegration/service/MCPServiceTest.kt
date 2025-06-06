package com.example.mcpintegration.service

import com.example.mcpintegration.model.FileResponse
import com.example.mcpintegration.model.ToolExecutionRequest
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MCPServiceTest {

    private val webClient = mockk<WebClient>()
    private val request = mockk<WebClient.RequestHeadersUriSpec<*>>()
    private val response = mockk<WebClient.ResponseSpec>()
    private val mcpService = MCPService(webClient)

    @Test
    fun `searchFiles should return list of files when successful`() {
        val query = "test"
        val fileResponse = listOf(
            FileResponse("test.txt", "/path/test.txt", 1024, LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
        )

        every { webClient.get() } returns request
        every { request.uri("/mcp/files/search?query={query}", query) } returns request
        every { request.retrieve() } returns response
        every { response.bodyToMono<List<FileResponse>>() } returns Mono.just(fileResponse)

        val result = mcpService.searchFiles(query)

        StepVerifier.create(result)
            .expectNext(fileResponse)
            .verifyComplete()
    }

    @Test
    fun `searchFiles should propagate error when request fails`() {
        val query = "test"
        val error = WebClientResponseException.create(500, "Internal Server Error", null, null, null)

        every { webClient.get() } returns request
        every { request.uri("/mcp/files/search?query={query}", query) } returns request
        every { request.retrieve() } returns response
        every { response.bodyToMono<List<FileResponse>>() } returns Mono.error(error)

        val result = mcpService.searchFiles(query)

        StepVerifier.create(result)
            .expectError(WebClientResponseException::class.java)
            .verify()
    }

    @Test
    fun `executeTool should return result when successful`() {
        val toolRequest = ToolExecutionRequest("exampleTool", mapOf("param1" to "value1"))
        val responseBody = "Tool executed successfully"

        every { webClient.post() } returns request
        every { request.uri("/mcp/tools/execute") } returns request
        every { request.bodyValue(toolRequest) } returns request
        every { request.retrieve() } returns response
        every { response.bodyToMono<String>() } returns Mono.just(responseBody)

        val result = mcpService.executeTool(toolRequest)

        StepVerifier.create(result)
            .expectNext(responseBody)
            .verifyComplete()
    }

    @Test
    fun `executeTool should propagate error when request fails`() {
        val toolRequest = ToolExecutionRequest("exampleTool", mapOf("param1" to "value1"))
        val error = WebClientResponseException.create(400, "Bad Request", null, null, null)

        every { webClient.post() } returns request
        every { request.uri("/mcp/tools/execute") } returns request
        every { request.bodyValue(toolRequest) } returns request
        every { request.retrieve() } returns response
        every { response.bodyToMono<String>() } returns Mono.error(error)

        val result = mcpService.executeTool(toolRequest)

        StepVerifier.create(result)
            .expectError(WebClientResponseException::class.java)
            .verify()
    }
}