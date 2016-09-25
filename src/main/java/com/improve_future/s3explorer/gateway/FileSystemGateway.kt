package com.improve_future.s3explorer.gateway

import java.io.File
import java.io.FilterInputStream
import java.nio.file.Files
import java.nio.file.StandardCopyOption

/**
 * Created by kenji on 16/09/25.
 */
class FileSystemGateway {
    fun save(filterInputStream: FilterInputStream, filePath: String) {
        val file = File("./tmp/" + filePath)
        file.mkdirs()
        Files.copy(
                filterInputStream,
                file.toPath(),
                StandardCopyOption.REPLACE_EXISTING)
    }
}