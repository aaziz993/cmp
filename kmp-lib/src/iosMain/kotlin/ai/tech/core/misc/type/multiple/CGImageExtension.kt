package ai.tech.core.misc.type.multiple

import ai.tech.core.data.model.ImageCompression
import kotlin.text.toInt
import kotlinx.cinterop.*
import platform.CoreGraphics.*
import platform.Foundation.*

import org.jetbrains.skia.*
import org.jetbrains.skia.ColorAlphaType
import org.jetbrains.skia.Image
import org.jetbrains.skia.ColorSpace
import org.jetbrains.skia.ImageInfo
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap


// /////////////////////////////////////////////////////////ARRAY///////////////////////////////////////////////////////
public fun CGImage.encodeRGBA(): ByteArray =
    ByteArray(width.toInt() * height.toInt() * 4).also {
        usePinned {
            CGContextDrawImage(
                CGBitmapContextCreate(
                    data = it.addressOf(0),
                    width = width.toInt(),
                    height = height.toInt(),
                    bitsPerComponent = 8,
                    bytesPerRow = (width * 4).toULong(),
                    space = colorSpace,
                    bitmapInfo = CGImageAlphaInfo.kCGImageAlphaPremultipliedLast.value.toUInt(),
                ),
                CGRectMake(0.0, 0.0, width.toDouble(), height.toDouble()),
                this,
            )
        }
    }

public fun CGImage.compress(
    compression: ImageCompression,
    quality: Float = 1f,
): ByteArray =
    UIImage
        .imageWithCGImage(cgImage)
        .let {
            when (compression) {
                ImageCompression.PNG -> it.pngData()
                ImageCompression.JPEG -> it.jpegData(quality)
            } as NSData
        }.encode()
