package ai.tech.core.misc.type.multiple

import ai.tech.core.data.model.ImageCompression
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.allocArrayOf
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.usePinned
import platform.CoreGraphics.CGBitmapContextCreate
import platform.CoreGraphics.CGBitmapContextCreateImage
import platform.CoreGraphics.CGColorSpaceCreateDeviceRGB
import platform.CoreGraphics.CGImageAlphaInfo
import platform.CoreGraphics.CGImageRef
import platform.Foundation.NSData
import platform.Foundation.create
import platform.UIKit.UIImage

public actual suspend fun ByteArray.compressRGBA(
    width: Number,
    height: Number,
    compression: ImageCompression,
): ByteArray = decodeCGImageRGBA(width.toInt(), height.toInt())!!.compress(compression)!!

public actual suspend fun ByteArray.decompressRGBA(compression: ImageCompression): ByteArray =
    decompressCGImage()!!.encodeRGBA()

// ////////////////////////////////////////////////////////NSDATA///////////////////////////////////////////////////////
@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
public fun ByteArray.decodeNSData(): NSData =
    memScoped {
        NSData.create(bytes = allocArrayOf(this@decodeNSData), length = this@decodeNSData.size.toULong())
    }

// ////////////////////////////////////////////////////////CGIMAGE//////////////////////////////////////////////////////
public fun ByteArray.decodeCGImageRGBA(
    width: Int,
    height: Int
): CGImageRef? = usePinned {
    // Create a bitmap context with the RGBA data
    val context = CGBitmapContextCreate(
        data = it.addressOf(0),
        width = width.toULong(),
        height = height.toULong(),
        bitsPerComponent = 8UL,
        bytesPerRow = width.toULong() * 4UL,
        space = CGColorSpaceCreateDeviceRGB(),
        bitmapInfo = CGImageAlphaInfo.kCGImageAlphaPremultipliedLast.value.toUInt(),
    )

    // Return a CGImage created from the context
    context?.let {
        CGBitmapContextCreateImage(it)
    }
}

public fun ByteArray.decompressCGImage(): CGImageRef? = UIImage.imageWithData(decodeNSData())?.CGImage
