package core.type.multiple

import java.awt.image.BufferedImage
import java.awt.image.DataBufferByte
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO

// /////////////////////////////////////////////////////////ARRAY///////////////////////////////////////////////////////
public fun BufferedImage.encodeRGBA(): ByteArray {
    require(type == BufferedImage.TYPE_4BYTE_ABGR) { "BufferedImage must be in TYPE_4BYTE_ABGR format" }
    val pixels = (this.raster.dataBuffer as DataBufferByte).data

    return ByteArray(pixels.size * 4).also {
        for (i in pixels.indices step 4) {
            it[i] = pixels[i + 3]
            it[i + 1] = pixels[i + 2]
            it[i + 2] = pixels[i + 1]
            it[i + 3] = pixels[i]
        }
    }
}

public fun BufferedImage.compress(formatName: String): ByteArray =
    ByteArrayOutputStream()
        .also {
            ImageIO.write(this, formatName, it)
        }.toByteArray()
