package com.example.mcpintegration.service

import com.example.mcpintegration.model.FileResponse
import com.example.mcpintegration.model.ToolExecutionRequest
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.Mono

@Service
class MCPService(private val webClient: WebClient) {

    fun searchFiles(query: String): Mono<List<FileResponse>> {
        return webClient.get()
            .uri("/mcp/files/search?query={query}", query)
            .retrieve()
            .bodyToMono<List<FileResponse>>()
    }

    fun executeTool(request: ToolExecutionRequest): Mono<String> {
        return webClient.post()
            .uri("/mcp/tools/execute")
            .bodyValue(request)
            .retrieve()
            .bodyToMono<String>()
    }
}