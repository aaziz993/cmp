package ai.tech.core.misc.type.multiple

import kotlin.text.get
import kotlin.text.toInt
import kotlinx.cinterop.ByteVar
import platform.CoreGraphics.*

public fun CMSampleBufferRef.encodeRGBA(): ByteArray {
    val imageBuffer = CMSampleBufferGetImageBuffer(buffer) ?: return ByteArray(0)
    CVPixelBufferLockBaseAddress(imageBuffer, kCVPixelBufferLock_ReadOnly)

    val baseAddress = CVPixelBufferGetBaseAddress(imageBuffer)
    val bytesPerRow = CVPixelBufferGetBytesPerRow(imageBuffer).toInt()
    val height = CVPixelBufferGetHeight(imageBuffer).toInt()

    val byteBuffer = baseAddress!!.reinterpret<ByteVar>()

    val byteArray = ByteArray(bytesPerRow * height)

    (byteArray.indeices step 4).forEach {
        byteArray[it] = byteBuffer[i + 2]
        byteArray[it + 1] = byteBuffer[i + 1]
        byteArray[it + 2] = byteBuffer[i + 0]
        byteArray[it + 3] = byteBuffer[i + 3]
    }

    CVPixelBufferUnlockBaseAddress(imageBuffer, kCVPixelBufferLock_ReadOnly)

    return byteArray
}

public fun CMSampleBufferRef.encodePCM(): ByteArray {
    val blockBuffer = CMSampleBufferGetDataBuffer(this) ?: return ByteArray(0)
    val audioBufferList = AudioBufferList()
    val size = CMBlockBufferGetDataLength(blockBuffer)
    val status =
        CMBlockBufferGetAudioBufferListWithBlock(
            blockBuffer,
            null,
            audioBufferList.refTo(0),
            size.toULong(),
            null,
            null,
            0,
            null,
        )
    if (status != kCMBlockBufferNoErr) {
        return ByteArray(0)
    }
    val audioBuffer = audioBufferList.mBuffers.pointed
    val data = ByteArray(audioBuffer.mDataByteSize)
    memcpy(data.refTo(0), audioBuffer.mData, audioBuffer.mDataByteSize.toULong())
    return data
}
