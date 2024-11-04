package ai.tech.core.presentation.component.map.model

import org.jxmapviewer.JXMapViewer
import org.jxmapviewer.painter.Painter
import java.awt.Color
import java.awt.Graphics2D
import java.awt.Rectangle

public class SelectionPainter(
    private val adapter: SelectionAdapter,
    private val onSelect: ((Rectangle) -> Unit)? = null
) :
    Painter<JXMapViewer> {
    private val fillColor = Color(128, 192, 255, 128)
    private val frameColor = Color(0, 0, 255, 128)

    override fun paint(g: Graphics2D, t: JXMapViewer?, width: Int, height: Int) {
        val rc: Rectangle? = adapter.getRectangle()

        if (rc != null) {
            g.color = frameColor
            g.draw(rc)
            g.color = fillColor
            g.fill(rc)
            onSelect?.invoke(rc)
        }
    }
}
