package ai.tech.core.misc.type.multiple

import web.canvas.CanvasRenderingContext2D
import web.dom.document
import web.html.HTMLCanvasElement
import web.images.ImageBitmap

// ////////////////////////////////////////////////////////CANVAS///////////////////////////////////////////////////////
public fun ImageBitmap.decodeToCanvasRenderingContext2D(context: CanvasRenderingContext2D): Unit = context.drawImage(this, 0.0, 0.0)

public fun ImageBitmap.decodeCanvas(): HTMLCanvasElement =
    (document.createElement("canvas") as HTMLCanvasElement).also {
        it.width = width
        it.height = height
        decodeToCanvasRenderingContext2D(it.getContext(CanvasRenderingContext2D.ID) as CanvasRenderingContext2D)
    }
