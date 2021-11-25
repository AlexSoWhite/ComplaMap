package com.example.complamap.model
import com.yandex.mapkit.Animation
import com.yandex.mapkit.map.*

class PlacemarkVisitor(
    private val focusRegion: VisibleRegion,
    private val zoom: Float
) : MapObjectVisitor {
    private val minZoom: Float = 13.168125F
    override fun onPlacemarkVisited(p0: PlacemarkMapObject) {
        if (zoom < minZoom) {
            p0.setVisible(false, Animation(Animation.Type.SMOOTH, 1F)) { }
            return
        }
        val point = p0.geometry
        val result = focusRegion.contains(point)
        p0.setVisible(result, Animation(Animation.Type.SMOOTH, 1F)) { }
    }
    override fun onPolylineVisited(p0: PolylineMapObject) {}
    override fun onColoredPolylineVisited(p0: ColoredPolylineMapObject) {}
    override fun onPolygonVisited(p0: PolygonMapObject) {}
    override fun onCircleVisited(p0: CircleMapObject) {}
    override fun onCollectionVisitStart(p0: MapObjectCollection) = true
    override fun onCollectionVisitEnd(p0: MapObjectCollection) {}
    override fun onClusterizedCollectionVisitStart(p0: ClusterizedPlacemarkCollection) = false
    override fun onClusterizedCollectionVisitEnd(p0: ClusterizedPlacemarkCollection) {}
}
