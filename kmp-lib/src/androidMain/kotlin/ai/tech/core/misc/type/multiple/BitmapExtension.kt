package ai.tech.core.misc.type.multiple

import android.graphics.Bitmap
import ai.tech.core.misc.type.single.sliceByte
import java.io.ByteArrayOutputStream

// ///////////////////////////////////////////////////////ARRAY/////////////////////////////////////////////////////////
public fun Bitmap.encodeRGBA(): ByteArray {
    require(config == Bitmap.Config.ARGB_8888) { "Bitmap must be in ARGB_8888 format" }

    val pixels = IntArray(width * height)

    getPixels(pixels, 0, width, 0, 0, width, height)

    pixels.iterator()

    return ByteArray(pixels.size * 4).also {
        var offset = 0

        pixels.forEach { p ->
            it[offset++] = p.sliceByte(16).toByte()
            it[offset++] = p.sliceByte(8).toByte()
            it[offset++] = p.sliceByte().toByte()
            it[offset++] = p.sliceByte(24).toByte()
        }
    }
}

public fun Bitmap.compress(
    format: Bitmap.CompressFormat,
    quality: Int = 100,
): ByteArray =
    ByteArrayOutputStream()
        .also {
            compress(format, quality, it)
            recycle()
        }.toByteArray()
