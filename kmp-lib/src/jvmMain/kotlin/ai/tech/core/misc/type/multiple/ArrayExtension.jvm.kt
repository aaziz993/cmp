package ai.tech.core.misc.type.multiple

import ai.tech.core.data.model.ImageCompression
import ai.tech.core.data.model.imageCompressionMap
import core.type.multiple.compress
import core.type.multiple.encodeRGBA
import java.awt.image.BufferedImage
import java.awt.image.DataBufferByte
import java.io.ByteArrayInputStream
import javax.imageio.ImageIO

public actual suspend fun ByteArray.compressRGBA(
    width: Number,
    height: Number,
    compression: ImageCompression,
): ByteArray = decodeBufferedImageABGR(width.toInt(), height.toInt()).compress(imageCompressionMap[compression]!!)

public actual suspend fun ByteArray.decompressRGBA(compression: ImageCompression): ByteArray = decompressBufferedImage().encodeRGBA()

// ///////////////////////////////////////////////////////BUFFEREDIMAGE/////////////////////////////////////////////////
public fun ByteArray.decodeBufferedImageABGR(
    width: Int,
    height: Int,
): BufferedImage =
    BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR).also {
        val pixels = (it.raster.dataBuffer as DataBufferByte).data
        (indices step 4).forEach {
            pixels[it] = this[it + 3]
            pixels[it + 1] = this[it + 2]
            pixels[it + 2] = this[it + 1]
            pixels[it + 3] = this[it]
        }
    }

public fun ByteArray.decompressBufferedImage(): BufferedImage = ImageIO.read(ByteArrayInputStream(this))
