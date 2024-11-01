package ai.tech.core.misc.type.multiple

import kotlin.text.get
import kotlin.text.toInt
import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.refTo
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.pointed
import platform.CoreMedia.CMSampleBufferRef
import platform.CoreMedia.CMSampleBufferGetImageBuffer
import platform.CoreMedia.CMSampleBufferGetDataBuffer
import platform.CoreMedia.CMBlockBufferGetDataLength
import platform.CoreMedia.kCMBlockBufferNoErr
import platform.CoreVideo.CVPixelBufferGetHeight
import platform.CoreVideo.CVPixelBufferLockBaseAddress
import platform.CoreVideo.CVPixelBufferGetBaseAddress
import platform.CoreVideo.CVPixelBufferGetBytesPerRow
import platform.CoreVideo.CVPixelBufferUnlockBaseAddress
import platform.CoreVideo.kCVPixelBufferLock_ReadOnly
import platform.CoreAudioTypes.AudioBufferList
import platform.posix.memcpy

public fun CMSampleBufferRef.encodeRGBA(): ByteArray {
    val imageBuffer = CMSampleBufferGetImageBuffer(this) ?: return ByteArray(0)
    CVPixelBufferLockBaseAddress(imageBuffer, kCVPixelBufferLock_ReadOnly)

    val baseAddress = CVPixelBufferGetBaseAddress(imageBuffer)
    val bytesPerRow = CVPixelBufferGetBytesPerRow(imageBuffer).toInt()
    val height = CVPixelBufferGetHeight(imageBuffer).toInt()

    val byteBuffer = baseAddress!!.reinterpret<ByteVar>()

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
