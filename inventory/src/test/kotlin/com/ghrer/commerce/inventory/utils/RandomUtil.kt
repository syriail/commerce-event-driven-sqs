package com.ghrer.commerce.inventory.utils

import kotlin.random.Random

object RandomUtil {

    const val ALLOWED_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
    const val ALLOWED_DIGITS = "0123456789"

    fun randomString(length: Int): String {
        return (1..length)
            .map { ALLOWED_CHARS.random() }
            .joinToString("")
    }

    fun randomWord(): String {
        val length = Random.nextInt(3, 10)
        return randomString(length)
    }

    fun randomItemName(): String {
        val numberOfWords = Random.nextInt(1, 5)
        return (1..numberOfWords).joinToString(" ") { randomWord() }
    }

    fun randomDescription(): String {
        val numberOfWords = Random.nextInt(1, 25)
        return (1..numberOfWords).joinToString(" ") { randomWord() }
    }
}
