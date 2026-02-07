package util

actual fun currentTimeSeconds(): Double = System.currentTimeMillis() / 1000.0
