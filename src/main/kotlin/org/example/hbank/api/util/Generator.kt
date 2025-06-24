package org.example.hbank.api.util

import kotlin.random.Random

object Generator {

    private val hexDigits =
        charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F')
    private val decDigits =
        charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9')

    fun generateHexString(length: Int = 6): String = IntRange(start = 1, endInclusive = length)
        .map { Random.nextInt(from = 0, until = hexDigits.size) }
        .map(transform = hexDigits::get)
        .joinToString(separator = String())

    fun generateDecString(length: Int = 6): String = IntRange(start = 1, endInclusive = length)
        .map { Random.nextInt(from = 0, until = decDigits.size) }
        .map(transform = decDigits::get)
        .joinToString(separator = String())
}
