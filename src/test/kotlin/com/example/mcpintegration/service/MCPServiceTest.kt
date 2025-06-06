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
    private val requestHeadersUriSpec = mockk<WebClient.RequestHeadersUriSpec<*>>()
    private val requestBodyUriSpec = mockk<WebClient.RequestBodyUriSpec>()
    private val requestBodySpec = mockk<WebClient.RequestBodySpec>()
    private val responseSpec = mockk<WebClient.ResponseSpec>()
    private val mcpService = MCPService(webClient)

    @Test
    fun `searchFiles should return list of files when successful`() {
        val query = "test"
        val fileResponse = listOf(
            FileResponse("test.txt", "/path/test.txt", 1024, LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
        )

        every { webClient.get() } returns requestHeadersUriSpec
        every { requestHeadersUriSpec.uri("/mcp/files/search?query={query}", query) } returns requestHeadersUriSpec
        every { requestHeadersUriSpec.retrieve() } returns responseSpec
        every { responseSpec.bodyToMono(List::class.java as Class<List<FileResponse>>) } returns Mono.just(fileResponse)

        val result = mcpService.searchFiles(query)

        StepVerifier.create(result)
            .expectNext(fileResponse)
            .verifyComplete()
    }

    @Test
    fun `searchFiles should propagate error when request fails`() {
        val query = "test"
        val error = WebClientResponseException.create(
            500,
            "Internal Server Error",
            org.springframework.http.HttpHeaders.EMPTY,
            ByteArray(0),
            null
        )

        every { webClient.get() } returns requestHeadersUriSpec
        every { requestHeadersUriSpec.uri("/mcp/files/search?query={query}", query) } returns requestHeadersUriSpec
        every { requestHeadersUriSpec.retrieve() } returns responseSpec
        every { responseSpec.bodyToMono(List::class.java as Class<List<FileResponse>>) } returns Mono.error(error)

        val result = mcpService.searchFiles(query)

        StepVerifier.create(result)
            .expectError(WebClientResponseException::class.java)
            .verify()
    }

    @Test
    fun `executeTool should return result when successful`() {
        val toolRequest = ToolExecutionRequest("exampleTool", mapOf("param1" to "value1"))
        val responseBody = "Tool executed successfully"

        every { webClient.post() } returns requestBodyUriSpec
        every { requestBodyUriSpec.uri("/mcp/tools/execute") } returns requestBodySpec
        every { requestBodySpec.bodyValue(toolRequest) } returns requestHeadersUriSpec
        every { requestHeadersUriSpec.retrieve() } returns responseSpec
        every { responseSpec.bodyToMono(String::class.java) } returns Mono.just(responseBody)

        val result = mcpService.executeTool(toolRequest)

        StepVerifier.create(result)
            .expectNext(responseBody)
            .verifyComplete()
    }

    @Test
    fun `executeTool should propagate error when request fails`() {
        val toolRequest = ToolExecutionRequest("exampleTool", mapOf("param1" to "value1"))
        val error = WebClientResponseException.create(
            400,
            "Bad Request",
            org.springframework.http.HttpHeaders.EMPTY,
            ByteArray(0),
            null
        )

        every { webClient.post() } returns requestBodyUriSpec
        every { requestBodyUriSpec.uri("/mcp/tools/execute") } returns requestBodySpec
        every { requestBodySpec.bodyValue(toolRequest) } returns requestHeadersUriSpec
        every { requestHeadersUriSpec.retrieve() } returns responseSpec
        every { responseSpec.bodyToMono(String::class.java) } returns Mono.error(error)

        val result = mcpService.executeTool(toolRequest)

        StepVerifier.create(result)
            .expectError(WebClientResponseException::class.java)
            .verify()
    }
}