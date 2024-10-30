package ai.tech.core.misc.type.multiple

import com.github.ajalt.colormath.Color
import com.github.ajalt.colormath.model.Ansi16
import com.github.ajalt.colormath.model.Ansi256
import com.github.ajalt.colormath.model.RGB
import ai.tech.core.misc.type.single.denormalizeByte
import ai.tech.core.misc.type.single.denormalizeInt
import ai.tech.core.misc.type.single.toByteArray

public fun Color.toByteArray(alpha: Boolean = true): ByteArray =
    toArray().dropLast(1).let {
        when (this) {
            is RGB -> {
                it.map { it.denormalizeByte() }.toByteArray()
            }

            is Ansi16, is Ansi256 -> byteArrayOf((it[0].toInt() - Byte.MAX_VALUE - 1).toByte())
            else -> it.fold(byteArrayOf()) { acc, v -> acc + v.denormalizeInt().toByteArray() }
        } + (if (alpha) byteArrayOf(this.alpha.denormalizeByte()) else byteArrayOf())
    }
