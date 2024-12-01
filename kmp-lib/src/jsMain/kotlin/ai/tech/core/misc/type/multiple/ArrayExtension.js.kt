package ai.tech.core.misc.type.multiple

import ai.tech.core.data.model.ImageCompression
import ai.tech.core.data.model.imageCompressionMap
import js.core.JsLong
import js.typedarrays.TypedArray
import js.typedarrays.Uint8ClampedArray
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import web.canvas.CanvasRenderingContext2D
import web.canvas.OffscreenCanvas
import web.canvas.OffscreenCanvasRenderingContext2D
import web.dom.document
import web.encoding.btoa
import web.events.EventHandler
import web.html.HTMLCanvasElement
import web.html.Image

public fun <S : TypedArray<S, T>, T : Comparable<T>, R> TypedArray<S, T>.fold(
    initial: R,
    operation: (acc: R, T) -> R,
): R {
    var accumulator = initial
    for (element in this) accumulator = operation(accumulator, element)
    return accumulator
}

public fun Uint8ClampedArray.toByteArray(): ByteArray =
    iterator()
        .toList()
        .toByteArray()

public fun ByteArray.toUint8ClampedArray(): Uint8ClampedArray = Uint8ClampedArray(toTypedArray())

public actual suspend fun ByteArray.compressRGBA(
    width: Number,
    height: Number,
    compression: ImageCompression,
): ByteArray = decodeCanvas(width.toInt(), height.toInt()).compress(imageCompressionMap[compression]!!)

public actual suspend fun ByteArray.decompressRGBA(compression: ImageCompression): ByteArray =
    decompressCanvas(imageCompressionMap[compression]!!).encodeRGBA()

// ////////////////////////////////////////////////////////CANVAS///////////////////////////////////////////////////////
public fun ByteArray.decodeToCanvasRenderingContext2D(
    context: CanvasRenderingContext2D,
    width: Int,
    height: Int,
): Unit =
    context.putImageData(
        context.createImageData(width, height).also {
            it.data.set(toUint8ClampedArray())
        },
        0,
        0,
    )

public fun ByteArray.decodeCanvas(
    width: Int,
    height: Int,
): HTMLCanvasElement =
    (document.createElement("canvas") as HTMLCanvasElement).also {
        it.width = width
        it.height = height
        decodeToCanvasRenderingContext2D(
            it.getContext(CanvasRenderingContext2D.ID) as CanvasRenderingContext2D,
            width,
            height,
        )
    }

public fun ByteArray.decodeToOffscreenCanvasRenderingContext2D(
    context: OffscreenCanvasRenderingContext2D,
    width: Int,
    height: Int,
): Unit =
    context.putImageData(
        context.createImageData(width, height).also {
            it.data.set(toUint8ClampedArray())
        },
        0,
        0,
    )

public fun ByteArray.decodeOffscreenCanvas(
    width: Int,
    height: Int,
): OffscreenCanvas =
    OffscreenCanvas(JsLong.fromBits(width.toLong()), JsLong.fromBits(height.toLong())).also {
        decodeToOffscreenCanvasRenderingContext2D(
            it.getContext(OffscreenCanvasRenderingContext2D.ID) as OffscreenCanvasRenderingContext2D,
            width,
            height,
        )
    }

@Suppress("UNREACHABLE_CODE")
public suspend fun ByteArray.decompressCanvas(type: String): HTMLCanvasElement =
    suspendCoroutine { continuation ->
        Image().apply {
            onload =
                EventHandler {
                    continuation.resume(
                        (document.createElement("canvas") as HTMLCanvasElement).also {
                            it.getContext(CanvasRenderingContext2D.ID)!!.drawImage(
                                this,
                                0.0,
                                0.0,
                                this.width.toDouble(),
                                this.height.toDouble(),
                            )
                        },
                    )
                }

            onerror = {
                continuation.resumeWithException(throw Exception("Unable decompress canvas"))
            }
            src = "data:$type;base64," + btoa(this@decompressCanvas.decode())
        }
    }
