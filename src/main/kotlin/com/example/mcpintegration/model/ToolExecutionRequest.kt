package com.example.mcpintegration.model

data class ToolExecutionRequest(
    val toolName: String,
    val parameters: Map<String, String>
)