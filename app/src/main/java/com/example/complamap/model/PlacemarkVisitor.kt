package com.example.complamap.model
import android.util.TypedValue
import com.example.complamap.R
import com.yandex.mapkit.Animation
import com.yandex.mapkit.map.CircleMapObject
import com.yandex.mapkit.map.ClusterizedPlacemarkCollection
import com.yandex.mapkit.map.ColoredPolylineMapObject
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.mapkit.map.MapObjectVisitor
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.mapkit.map.PolygonMapObject
import com.yandex.mapkit.map.PolylineMapObject
import com.yandex.mapkit.map.VisibleRegion

class PlacemarkVisitor(
    private val focusRegion: VisibleRegion,
    private val zoom: Float
) : MapObjectVisitor {
    private val minZoom: Float
    init {
        val outValue = TypedValue()
        ContextContainer.getContext().resources.getValue(R.dimen.min_zoom, outValue, true)
        minZoom = outValue.float
    }
    override fun onPlacemarkVisited(p0: PlacemarkMapObject) {
        if (zoom < minZoom) {
            p0.setVisible(false, Animation(Animation.Type.SMOOTH, 1F)) { }
            return
        }
        val point = p0.geometry
        val result = focusRegion.contains(point)
        p0.setVisible(result, Animation(Animation.Type.SMOOTH, 1F)) { }
    }
    override fun onPolylineVisited(p0: PolylineMapObject) { // Polyline в проекте не используется
    }
    override fun onColoredPolylineVisited(p0: ColoredPolylineMapObject) {
        // ColoredPolyline в проекте не используется
    }
    override fun onPolygonVisited(p0: PolygonMapObject) {
        // Polygon в проекте не используется
    }
    override fun onCircleVisited(p0: CircleMapObject) {
        // Circle в проекте не используется
    }
    override fun onCollectionVisitStart(p0: MapObjectCollection) = true
    override fun onCollectionVisitEnd(p0: MapObjectCollection) {
        // В переопределении этого медота нет надобности
    }
    override fun onClusterizedCollectionVisitStart(p0: ClusterizedPlacemarkCollection) = false
    override fun onClusterizedCollectionVisitEnd(p0: ClusterizedPlacemarkCollection) {
        // ClusterizedCollection в проекте не используется
    }
}
