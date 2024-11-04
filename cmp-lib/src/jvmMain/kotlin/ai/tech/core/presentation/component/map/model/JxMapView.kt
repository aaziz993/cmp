package ai.tech.core.presentation.component.map.model

import ai.tech.core.misc.location.model.Location
import ai.tech.core.misc.type.multiple.outersectUpdate
import org.jxmapviewer.JXMapViewer
import org.jxmapviewer.OSMTileFactoryInfo
import org.jxmapviewer.VirtualEarthTileFactoryInfo
import org.jxmapviewer.cache.FileBasedLocalCache
import org.jxmapviewer.google.GoogleMapsTileFactoryInfo
import org.jxmapviewer.input.CenterMapListener
import org.jxmapviewer.input.PanKeyListener
import org.jxmapviewer.input.PanMouseInputListener
import org.jxmapviewer.input.ZoomMouseWheelListenerCursor
import org.jxmapviewer.painter.CompoundPainter
import org.jxmapviewer.painter.Painter
import org.jxmapviewer.viewer.DefaultTileFactory
import org.jxmapviewer.viewer.GeoPosition
import org.jxmapviewer.viewer.TileFactory
import org.jxmapviewer.viewer.TileFactoryInfo
import java.awt.GridLayout
import java.awt.Rectangle
import java.awt.geom.Point2D
import java.io.File
import javax.swing.JComboBox
import javax.swing.JLabel
import javax.swing.JPanel

public class JxMapView(
    initialCenter: GeoPosition? = null,
    initialZoom: Int? = null,
    zoomable: Boolean = true,
    moveable: Boolean = true,
    tilePicker: Boolean = true,
    googleApiKey: String? = null,
    markers: Set<SwingWaypoint>? = null,
    routes: List<List<GeoPosition>>? = null,
    onSelect: ((Set<SwingWaypoint>, Set<SwingWaypoint>) -> Unit)? = null,
    localization: MapViewLocalization = MapViewLocalization(),
) : JXMapViewer() {

    private val factories = listOf(
        localization.virtualEarthMap to VirtualEarthTileFactoryInfo(VirtualEarthTileFactoryInfo.MAP).toCachedDefaultTileFactory(),
        localization.openStreetMap to OSMTileFactoryInfo().toCachedDefaultTileFactory(),
        localization.googleMap to googleApiKey?.let { GoogleMapsTileFactoryInfo(it).toCachedDefaultTileFactory() },
    ).filterNot { it.second == null }

    private val selectedMarkers = mutableListOf<SwingWaypoint>()

    init {
        tileFactory = factories[0].second

        initialZoom?.let { zoom = it }
        initialCenter?.let { addressLocation = it }

        // Event listeners
        if (zoomable) {
            addMouseWheelListener(ZoomMouseWheelListenerCursor(this))
        }

        if (moveable) {
            PanMouseInputListener(this).let {
                addMouseListener(it)
                addMouseMotionListener(it)
            }
            addMouseListener(CenterMapListener(this))
            addKeyListener(PanKeyListener(this))
        }

        overlayPainter = CompoundPainter(mutableListOf<Painter<JXMapViewer>>().apply {

            markers?.let { add(SwingWaypointPainter(this@JxMapView, it)) }

            routes?.forEach { add(RoutePainter(it)) }

            onSelect?.let { os ->
                add(
                    SelectionPainter(SelectionAdapter(this@JxMapView).also {
                        addMouseListener(it)
                        addMouseMotionListener(it)
                    }, markers?.let { ms ->
                        { rectangle ->
                            selectedMarkers.outersectUpdate(ms.filter {
                                isSelected(it.position, rectangle)
                            }).let { os(it.first.toSet(), it.second.toSet()) }
                        }
                    })
                )
            }
        })

        val tileLicenseLabel = JLabel(tileFactory.toLicenseText())

        if (tilePicker) {
            add(
                JPanel().apply {
                    layout = GridLayout()
                    add(JLabel(localization.selectTile))
                    add(JComboBox(factories.map { it.first }.toTypedArray()).apply {
                        addItemListener {
                            tileFactory = factories[selectedIndex].second
                            initialZoom?.let { zoom = it }
                            tileLicenseLabel.setText(tileFactory.toLicenseText())
                        }
                    })
                }
            )
        }

        add(tileLicenseLabel)
    }


    public fun toScreenPixel(geoPosition: GeoPosition): Point2D = tileFactory.geoToPixel(geoPosition, zoom).let {
        Point2D.Double(it.x - center.x + width / 2, it.y - center.y + height / 2)
    }

    public fun isSelected(geoPosition: GeoPosition, rectangle: Rectangle): Boolean = toScreenPixel(geoPosition).let {
        it.x.toInt() in rectangle.x until rectangle.x + rectangle.width && it.y.toInt() in rectangle.y until rectangle.y + rectangle.height
    }

}

private fun TileFactoryInfo.toCachedDefaultTileFactory() = DefaultTileFactory(this).apply {
    setLocalCache(
        FileBasedLocalCache(
            File(System.getProperty("user.home") + File.separator + ".$name" + ".jxmapviewer2"), false
        )
    )
    setThreadPoolSize(8)
}

private fun TileFactory.toLicenseText() = info.attribution + " - " + info.license

public fun Location.toGeoPosition(): GeoPosition = GeoPosition(latitude, longitude)
