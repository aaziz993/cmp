package ai.tech.core.presentation.component.map.model

import org.jxmapviewer.JXMapViewer
import java.awt.Rectangle
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.geom.Point2D

public class SelectionAdapter(private val viewer: JXMapViewer) : MouseAdapter() {
    private var dragging = false
    private val startPos = Point2D.Double()
    private val endPos = Point2D.Double()

    override fun mousePressed(e: MouseEvent) {
        if (e.button != MouseEvent.BUTTON3) return

        startPos.setLocation(e.x.toDouble(), e.y.toDouble())
        endPos.setLocation(e.x.toDouble(), e.y.toDouble())

        dragging = true
    }

    override fun mouseDragged(e: MouseEvent) {
        if (!dragging) return

        endPos.setLocation(e.x.toDouble(), e.y.toDouble())

        viewer.repaint()
    }

    override fun mouseReleased(e: MouseEvent) {
        if (!dragging) return

        if (e.button != MouseEvent.BUTTON3) return

        viewer.repaint()

        dragging = false
    }

    /**
     * @return the selection rectangle
     */
    public fun getRectangle(): Rectangle? {
        return if (dragging) {
            val x1 = minOf(startPos.x, endPos.x).toInt()
            val y1 = minOf(startPos.y, endPos.y).toInt()
            val x2 = maxOf(startPos.x, endPos.x).toInt()
            val y2 = maxOf(startPos.y, endPos.y).toInt()

            Rectangle(x1, y1, x2 - x1, y2 - y1)
        } else {
            null
        }
    }
}
