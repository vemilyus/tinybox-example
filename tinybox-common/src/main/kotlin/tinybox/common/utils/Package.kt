package tinybox.common.utils

import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import kotlin.math.ceil
import java.util.Random as JRandom

private val random: JRandom = try {
    SecureRandom.getInstanceStrong()
} catch (_: NoSuchAlgorithmException) {
    JRandom()
}

private val HEX_NUMBERS = "0123456789ABCDEF".toCharArray()

fun ByteArray.toHex(): String {
    val hexChars = CharArray(size * 2)

    for (j in indices) {
        val v: Int = this[j].toInt() and 0xFF
        hexChars[j shl 1] = HEX_NUMBERS[v ushr 4]
        hexChars[(j shl 1) + 1] = HEX_NUMBERS[v and 0x0F]
    }

    return String(hexChars)
}

fun generateRandomString(length: Int = 32): String {
    val bytes = ByteArray(ceil(length.toFloat() / 2).toInt())
    random.nextBytes(bytes)

    val bytesString = bytes.toHex()
    return bytesString.substring(0, length)
}
