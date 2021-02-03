package tinybox.auth.utils

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
