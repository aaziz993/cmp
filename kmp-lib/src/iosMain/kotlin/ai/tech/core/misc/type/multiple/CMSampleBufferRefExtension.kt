package ai.tech.core.misc.type.multiple

import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.get
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.usePinned
import platform.CoreMedia.CMBlockBufferCopyDataBytes
import platform.CoreMedia.CMBlockBufferGetDataLength
import platform.CoreMedia.CMSampleBufferGetDataBuffer
import platform.CoreMedia.CMSampleBufferGetImageBuffer
import platform.CoreMedia.CMSampleBufferRef
import platform.CoreVideo.CVPixelBufferGetBaseAddress
import platform.CoreVideo.CVPixelBufferGetBytesPerRow
import platform.CoreVideo.CVPixelBufferGetHeight
import platform.CoreVideo.CVPixelBufferLockBaseAddress
import platform.CoreVideo.CVPixelBufferUnlockBaseAddress
import platform.CoreVideo.kCVPixelBufferLock_ReadOnly

public fun CMSampleBufferRef.encodeRGBA(): ByteArray {
    val imageBuffer = CMSampleBufferGetImageBuffer(this) ?: return ByteArray(0)
    CVPixelBufferLockBaseAddress(imageBuffer, kCVPixelBufferLock_ReadOnly)

    try {
        val baseAddress = CVPixelBufferGetBaseAddress(imageBuffer) ?: return ByteArray(0)
        val bytesPerRow = CVPixelBufferGetBytesPerRow(imageBuffer).toInt()
        val height = CVPixelBufferGetHeight(imageBuffer).toInt()

        val byteBuffer = baseAddress.reinterpret<ByteVar>()

        val byteArray = ByteArray(bytesPerRow * height)

        (byteArray.indices step 4).forEach {
            byteArray[it] = byteBuffer[it + 2]
            byteArray[it + 1] = byteBuffer[it + 1]
            byteArray[it + 2] = byteBuffer[it + 0]
            byteArray[it + 3] = byteBuffer[it + 3]
        }

        CVPixelBufferUnlockBaseAddress(imageBuffer, kCVPixelBufferLock_ReadOnly)

        return byteArray
    }
    finally {
        // Unlock the base address
        CVPixelBufferUnlockBaseAddress(imageBuffer, kCVPixelBufferLock_ReadOnly)
    }
}

public fun CMSampleBufferRef.encodePCM(): ByteArray {
    val blockBuffer = CMSampleBufferGetDataBuffer(this) ?: return ByteArray(0)

    val size = CMBlockBufferGetDataLength(blockBuffer)

    return ByteArray(size.toInt()).apply {
        usePinned {
            CMBlockBufferCopyDataBytes(blockBuffer, 0UL, size, it.addressOf(0))
        }
    }
}
