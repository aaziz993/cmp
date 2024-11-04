package ai.tech.core.presentation.component.map.model

import org.jxmapviewer.JXMapViewer
import org.jxmapviewer.viewer.WaypointPainter
import java.awt.Graphics2D
import java.awt.Rectangle
import java.awt.geom.Point2D

public class SwingWaypointPainter(jxMapViewer: JXMapViewer, waypoints: Set<SwingWaypoint>) :
    WaypointPainter<SwingWaypoint>() {

    init {
        this.waypoints = waypoints
        this.waypoints.forEach { jxMapViewer.add(it.button) }
    }

    override fun doPaint(g: Graphics2D, jxMapViewer: JXMapViewer, width: Int, height: Int): Unit =
        waypoints.forEach {
            val point: Point2D = jxMapViewer.tileFactory.geoToPixel(it.position, jxMapViewer.zoom)
            val rectangle: Rectangle = jxMapViewer.viewportBounds
            val buttonX = (point.x - rectangle.x).toInt()
            val buttonY = (point.y - rectangle.y).toInt()
            it.button.setLocation(buttonX + it.offset.width, buttonY + it.offset.height)
        }
}

