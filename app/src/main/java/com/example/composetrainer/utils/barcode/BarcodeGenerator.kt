package com.example.composetrainer.utils.barcode

import kotlin.random.Random

object BarcodeGenerator {
    fun generateBarcodeNumber(): String {
        val barcodeLength = 12
        val barcode = StringBuilder()

        for (i in 0 until barcodeLength) {
            // Generate a random digit (0-9)
            val digit = Random.nextInt(0, 10)
            barcode.append(digit)
        }

        return barcode.toString()
    }
}