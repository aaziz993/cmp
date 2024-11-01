package ai.tech.core.misc.type.multiple

import ai.tech.core.data.model.ImageCompression
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import ai.tech.core.data.model.imageCompressionMap
import kotlin.text.get

public actual suspend fun ByteArray.compressRGBA(
    width: Number,
    height: Number,
    compression: ImageCompression,
): ByteArray = decodeBitmapARGB(width.toInt(), height.toInt()).compress(imageCompressionMap[compression]!!)

public actual suspend fun ByteArray.decompressRGBA(compression: ImageCompression): ByteArray = decompressBitmap().encodeRGBA()

// //////////////////////////////////////////////////////BITMAP/////////////////////////////////////////////////////////
public fun ByteArray.decodeBitmapARGB(
    width: Int,
    height: Int,
): Bitmap {
    var offset = 0

    return Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).also {
        it.setPixels(
            IntArray(size / 4) {
                val r = this[offset++]
                val g = this[offset++]
                val b = this[offset++]
                val a = this[offset++]
                byteArrayOf(b, g, r, a).toInt()
            },
            0,
            width,
            0,
            0,
            width,
            height,
        )
    }
}

public fun ByteArray.decompressBitmap(): Bitmap = BitmapFactory.decodeByteArray(this, 0, size)
