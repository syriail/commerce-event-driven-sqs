package com.ghrer.commerce.orders.util

import org.springframework.core.io.ClassPathResource
import java.io.BufferedReader
import java.io.FileNotFoundException
import java.io.InputStreamReader

object FileUtil {
    fun readJsonFileAsString(filePath: String): String? {
        val resource = ClassPathResource(filePath)
        if (resource.exists()) {
            BufferedReader(InputStreamReader(resource.inputStream)).use {
                return it.readText()
            }
        }
        throw FileNotFoundException(filePath)
    }
}
