package ai.tech.core.misc.type.multiple

import ai.tech.core.data.model.AudioFormat
import ai.tech.core.misc.type.Object
import ai.tech.core.misc.type.multiple.model.AbstractAsyncIterator
import ai.tech.core.misc.type.multiple.model.AsyncIterator
import ai.tech.core.misc.type.multiple.model.AbstractClosableAbstractAsyncIterator
import ai.tech.core.misc.type.multiple.model.AbstractClosableAbstractIterator
import ai.tech.core.misc.type.single.denormalizeInt
import ai.tech.core.misc.type.single.toByteArray
import js.typedarrays.Float32Array
import kotlinx.coroutines.channels.Channel
import web.audio.AudioContext
import web.audio.AudioWorkletNode
import web.canvas.CanvasRenderingContext2D
import web.dom.document
import web.events.EventHandler
import web.html.HTMLCanvasElement
import web.html.HTMLVideoElement
import web.media.streams.MediaStream
import web.streams.ReadableStream

// ////////////////////////////////////////////////////ASYNCITERATOR////////////////////////////////////////////////////
public fun <T> ReadableStream<T>.asyncIterator(): AsyncIterator<T> = ReadableStreamAsyncIterator(this)

private class ReadableStreamAsyncIterator<T>(
    stream: ReadableStream<T>,
) : AbstractAsyncIterator<T>() {

    private val reader = stream.getReader()

    override suspend fun computeNext() {
        reader.read().let {
            if (it.asDynamic().done as Boolean) {
                done()
            }
            else {
                setNext(it.asDynamic().value as T)
            }
        }
    }
}

// //////////////////////////////////////////////////////MEDIASTREAM////////////////////////////////////////////////////

public fun MediaStream.rgbaIterator(
    width: Double,
    height: Double,
    x: Double = 0.0,
    y: Double = 0.0,
): AbstractClosableAbstractIterator<ByteArray> =
    MediaStreamImageIterator(
        this,
        width,
        height,
        x,
        y,
    )

private class MediaStreamImageIterator(
    private val mediaStream: MediaStream,
    private val width: Double,
    private val height: Double,
    private val x: Double = 0.0,
    private val y: Double = 0.0,
) : AbstractClosableAbstractIterator<ByteArray>() {

    val canvas = document.createElement("canvas") as HTMLCanvasElement
    val context = canvas.getContext(CanvasRenderingContext2D.ID) as CanvasRenderingContext2D
    val video =
        (document.createElement("video") as HTMLVideoElement).also {
            it.srcObject = mediaStream
        }

    override fun computeNext() {
        context.drawImage(video, x, y, width, height)
        setNext(context.encodeRGBA())
    }

    override fun close() {
        super.close()
        mediaStream.getTracks().forEach { it.stop() }
        video.srcObject = null
    }
}

public fun MediaStream.audioIterator(
    audioFormat: AudioFormat,
    bufferSize: Int,
    audioContext: AudioContext,
): AbstractClosableAbstractAsyncIterator<ByteArray> =
    MediaStreamAudioAsyncIterator(
        this,
        audioFormat,
        bufferSize,
        audioContext,
    )

private class MediaStreamAudioAsyncIterator(
    private val mediaStream: MediaStream,
    private val audioFormat: AudioFormat,
    bufferSize: Int,
    private val audioContext: AudioContext,
) : AbstractClosableAbstractAsyncIterator<ByteArray>() {

    private val channel = Channel<ByteArray>(1)
    private val sampleSizeInBytes = audioFormat.sampleSizeInBits / 8
    private val maxValue = 1 shl (audioFormat.sampleSizeInBits - 1)

    private val audioWorkletNode =
        AudioWorkletNode(
            audioContext,
            "audio-input-processor",
            Object {
                numberOfInputs = audioFormat.channelCount
            },
        ).also {
            it.connect(audioContext.destination)
            it.port.onmessage =
                EventHandler { event ->
                    channel.trySend(
                        (event.data as Array<Float32Array>).let {
                            merge(
                                it
                                    .map {
                                        it.subarray(0, bufferSize).fold(byteArrayOf()) { acc, v ->
                                            acc + v.denormalizeInt(maxValue).toByteArray(sampleSizeInBytes)
                                        }
                                    },
                            )
                        },
                    )
                }
        }

    private val mediaStreamSource =
        audioContext.createMediaStreamSource(mediaStream).apply {
            connect(audioWorkletNode)
        }

    override suspend fun computeNext() {
        setNext(channel.receive())
    }

    override fun close() {
        super.close()
        mediaStreamSource.disconnect()
        audioWorkletNode.disconnect()
        mediaStream.getTracks().forEach { it.stop() }
    }
}
