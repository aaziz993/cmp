package ai.tech.core.misc.type.multiple

import ai.tech.core.data.model.ImageCompression
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.CoreGraphics.*
import platform.CoreGraphics.CGImageRef
import platform.UIKit.UIImage
import platform.UIKit.UIImageJPEGRepresentation
import platform.UIKit.UIImagePNGRepresentation

// /////////////////////////////////////////////////////////ARRAY///////////////////////////////////////////////////////

public fun CGImageRef.encodeRGBA(): ByteArray {
    val width = CGImageGetWidth(this)
    val height = CGImageGetHeight(this)
    // Create a ByteArray to hold the RGBA data
    return ByteArray(width.toInt() * height.toInt() * 4).also { byteArray ->
        // Pin the ByteArray to obtain a stable memory address
        byteArray.usePinned { pinned ->
            // Create a bitmap context with RGBA color space
            val context = CGBitmapContextCreate(
                data = pinned.addressOf(0),
                width = width,
                height = height,
                bitsPerComponent = 8UL,
                bytesPerRow = width.toULong() * 4UL,
                space = CGColorSpaceCreateDeviceRGB(),
                bitmapInfo = CGImageAlphaInfo.kCGImageAlphaPremultipliedLast.value.toUInt(),
            )
            context?.let {
                // Draw the CGImage into the context
                CGContextDrawImage(it, CGRectMake(0.0, 0.0, width.toDouble(), height.toDouble()), this)
            }
        }
    }
}

public fun CGImageRef.compress(
    compression: ImageCompression,
    quality: Double = 1.0,
): ByteArray? =
    UIImage.imageWithCGImage(this).let {
        when (compression) {
            ImageCompression.PNG -> UIImagePNGRepresentation(it)
            ImageCompression.JPEG -> UIImageJPEGRepresentation(it, quality)
        }?.encode()
    }
