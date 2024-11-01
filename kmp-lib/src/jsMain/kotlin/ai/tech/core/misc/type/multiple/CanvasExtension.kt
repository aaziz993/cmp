package ai.tech.core.misc.type.multiple

import web.canvas.CanvasRenderingContext2D
import web.canvas.OffscreenCanvas
import web.canvas.OffscreenCanvasRenderingContext2D
import web.encoding.atob
import web.html.HTMLCanvasElement

// ////////////////////////////////////////////////////////ARRAY////////////////////////////////////////////////////////
public fun CanvasRenderingContext2D.encodeRGBA(): ByteArray =
    getImageData(0, 0, canvas.width, canvas.height)
        .data
        .toByteArray()

public fun HTMLCanvasElement.encodeRGBA(): ByteArray =
    (getContext(CanvasRenderingContext2D.ID) as CanvasRenderingContext2D)
        .encodeRGBA()

public fun OffscreenCanvasRenderingContext2D.encodeRGBA(): ByteArray =
    getImageData(0, 0, canvas.width.toInt(), canvas.height.toInt())
        .data
        .toByteArray()

public fun OffscreenCanvas.encodeRGBA(): ByteArray =
    (getContext(OffscreenCanvasRenderingContext2D.ID) as OffscreenCanvasRenderingContext2D).encodeRGBA()

public fun HTMLCanvasElement.compress(
    type: String,
    quality: Double? = null,
): ByteArray = atob((quality?.let { toDataURL(type, quality) } ?: toDataURL(type)).substringAfter("base64,")).encode()
