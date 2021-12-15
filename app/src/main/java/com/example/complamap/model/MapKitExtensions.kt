package com.example.complamap.model

import android.util.TypedValue
import com.example.complamap.R
import com.yandex.mapkit.Animation
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.map.VisibleRegion

private val standartZoom: Float
    get() {
        val outValue = TypedValue()
        ContextContainer.getContext().resources.getValue(R.dimen.standart_zoom, outValue, true)
        return outValue.float
    }

fun VisibleRegion.contains(vecM: Point): Boolean {
    val vecAM = Point(
        vecM.latitude - topLeft.latitude,
        vecM.longitude - topLeft.longitude
    )
    val vecAB = Point(
        topRight.latitude - topLeft.latitude,
        topRight.longitude - topLeft.longitude
    )
    val vecAD = Point(
        bottomLeft.latitude - topLeft.latitude,
        bottomLeft.longitude - topLeft.longitude
    )
    val vecAMprodAB = vecAM.latitude * vecAB.latitude + vecAM.longitude * vecAB.longitude
    val vecABprodAB = vecAB.latitude * vecAB.latitude + vecAB.longitude * vecAB.longitude
    val vecAMprodAD = vecAM.latitude * vecAD.latitude + vecAM.longitude * vecAD.longitude
    val vecADprodAD = vecAD.latitude * vecAD.latitude + vecAD.longitude * vecAD.longitude

    return ((0 < vecAMprodAB) && (vecAMprodAB < vecABprodAB)) &&
        ((0 < vecAMprodAD) && (vecAMprodAD < vecADprodAD))
}

fun Map.moveIfNotGreater(point: Point) {
    if (this.cameraPosition.zoom >= standartZoom) {
        this.move(
            CameraPosition(
                Point(
                    point.latitude,
                    point.longitude
                ),
                this.cameraPosition.zoom,
                0F,
                0F
            ),
            Animation(Animation.Type.SMOOTH, 1F),
            null
        )
        return
    }
    this.move(
        CameraPosition(
            Point(
                point.latitude,
                point.longitude
            ),
            standartZoom,
            0F,
            0F
        ),
        Animation(Animation.Type.SMOOTH, 1F),
        null
    )
}

fun Map.moveTo(point: Point, zoom: Float = standartZoom) {
    this.move(
        CameraPosition(
            Point(
                point.latitude,
                point.longitude
            ),
            zoom,
            0F,
            0F
        ),
        Animation(Animation.Type.SMOOTH, 1F),
        null
    )
}
