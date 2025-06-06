package com.example.mcpintegration.model

data class FileResponse(
    val fileName: String,
    val filePath: String,
    val size: Long,
    val lastModified: String
)