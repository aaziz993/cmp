package ai.tech.core.misc.type.multiple

import web.canvas.CanvasRenderingContext2D
import web.codecs.VideoFrame
import web.dom.document
import web.html.HTMLCanvasElement

// ////////////////////////////////////////////////////////CANVAS///////////////////////////////////////////////////////
public fun VideoFrame.decodeToCanvasRenderingContext2D(context: CanvasRenderingContext2D): Unit =
    context.drawImage(
        this,
        visibleRect?.x ?: 0.0,
        visibleRect?.y ?: 0.0,
        visibleRect?.width ?: displayWidth.toDouble(),
        visibleRect?.height ?: displayHeight.toDouble(),
        0.0,
        0.0,
        context.canvas.width.toDouble(),
        context.canvas.height.toDouble(),
    )

public fun VideoFrame.decodeCanvas(): HTMLCanvasElement =
    (document.createElement("canvas") as HTMLCanvasElement).also {
        it.width = (visibleRect?.width?.toInt() ?: displayWidth) - (visibleRect?.x?.toInt() ?: 0)
        it.height = (visibleRect?.height?.toInt() ?: displayHeight) - (visibleRect?.y?.toInt() ?: 0)
        decodeToCanvasRenderingContext2D(it.getContext(CanvasRenderingContext2D.ID) as CanvasRenderingContext2D)
    }
