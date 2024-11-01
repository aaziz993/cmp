package ai.tech.core.misc.type.multiple

import ai.tech.core.data.model.ImageCompression
import ai.tech.core.misc.type.multiple.model.Charset
import ai.tech.core.misc.type.multiple.model.charsetMap
import kotlinx.cinterop.*
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.allocArrayOf
import kotlinx.cinterop.memScoped
import platform.CoreGraphics.*
import platform.Foundation.*

public actual suspend fun ByteArray.compressRGBA(
    width: Number,
    height: Number,
    compression: ImageCompression,
): ByteArray = decodeCGImageRGBA(width.toInt(), height.toInt()).compress(compression)

public actual suspend fun ByteArray.decompressRGBA(compression: ImageCompression): ByteArray =
    decompressCGImage()!!.encodeRGBA(compression)

// ////////////////////////////////////////////////////////NSDATA///////////////////////////////////////////////////////
@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
public fun ByteArray.decodeNSData(): NSData =
    memScoped {
        NSData.create(bytes = allocArrayOf(this@decodeNSData), length = this@decodeNSData.size.toULong())
    }

// ////////////////////////////////////////////////////////CGIMAGE//////////////////////////////////////////////////////
@OptIn(ExperimentalForeignApi::class)
public fun ByteArray.decodeCGImageRGBA(
    width: Int,
    height: Int,
): CGImage? =
    usePinned {
        CGBitmapContextCreate(
            data = it.addressOf(0),
            width = width,
            height = height,
            bitsPerComponent = 8,
            bytesPerRow = (width * 4).toULong(),
            space = CGColorSpaceCreateDeviceRGB(),
            bitmapInfo = CGImageAlphaInfo.kCGImageAlphaPremultipliedLast.value.toUInt(),
        )?.makeImage()
    }

public fun ByteArray.decompressCGImage(): CGImage? = UIImage.imageWithData(decodeNSData())?.CGImage

// ////////////////////////////////////////////////////////STRING///////////////////////////////////////////////////////
public actual fun ByteArray.decode(charset: Charset): String =
    charsetMap[charset]?.let {
        decodeNSData().decode(it)
    } ?: throw IllegalArgumentException("Unsupported charset encoding: ${charset.name}")
