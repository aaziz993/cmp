package ai.tech.core.presentation.component.map.model

import org.jxmapviewer.JXMapViewer
import org.jxmapviewer.painter.Painter
import org.jxmapviewer.viewer.GeoPosition
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Graphics2D
import java.awt.Rectangle
import java.awt.RenderingHints
import java.awt.geom.Point2D

public class RoutePainter(private val track: List<GeoPosition>) : Painter<JXMapViewer> {
    private val color: Color = Color.RED
    private val antiAlias: Boolean = true

    override fun paint(g: Graphics2D, map: JXMapViewer, w: Int, h: Int) {
        val g2d = g.create() as Graphics2D

        // convert from viewport to world bitmap
        val rect: Rectangle = map.viewportBounds
        g2d.translate(-rect.x, -rect.y)

        if (antiAlias) {
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        }

        // do the drawing
        g2d.color = Color.BLACK
        g2d.stroke = BasicStroke(4f)

        drawRoute(g2d, map)

        // do the drawing again
        g2d.color = color
        g2d.stroke = BasicStroke(2f)

        drawRoute(g2d, map)

        g2d.dispose()
    }

    /**
     * @param g the graphics object
     * @param map the map
     */
    private fun drawRoute(g: Graphics2D, map: JXMapViewer) {
        var lastX = 0
        var lastY = 0

        var first = true

        for (gp in track) {
            // convert geo-coordinate to world bitmap pixel
            val pt: Point2D = map.tileFactory.geoToPixel(gp, map.zoom)

            if (first) {
                first = false
            } else {
                g.drawLine(lastX, lastY, pt.x.toInt(), pt.y.toInt())
            }

            lastX = pt.x.toInt()
            lastY = pt.y.toInt()
        }
    }
}
