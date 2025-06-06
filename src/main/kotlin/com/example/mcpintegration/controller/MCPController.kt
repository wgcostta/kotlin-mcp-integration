package com.example.mcpintegration.controller

import com.example.mcpintegration.model.FileResponse
import com.example.mcpintegration.model.ToolExecutionRequest
import com.example.mcpintegration.service.MCPService
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/mcp")
class MCPController(private val mcpService: MCPService) {

    @GetMapping("/files/search")
    fun searchFiles(@RequestParam query: String): Mono<List<FileResponse>> {
        return mcpService.searchFiles(query)
    }

    @PostMapping("/tools/execute")
    fun executeTool(@RequestBody request: ToolExecutionRequest): Mono<String> {
        return mcpService.executeTool(request)
    }
}