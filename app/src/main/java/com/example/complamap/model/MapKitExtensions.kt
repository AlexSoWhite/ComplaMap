package com.example.complamap.model

import android.content.Context
import android.media.Image
import android.util.Log
import android.util.TypedValue
import androidx.core.content.ContentProviderCompat.requireContext
import com.example.complamap.R
import com.yandex.mapkit.Animation
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.map.VisibleRegion
import com.yandex.runtime.image.ImageProvider

private val standartZoom: Float
    get() {
        val outValue = TypedValue()
        ContextContainer.getContext().resources.getValue(R.dimen.standart_zoom, outValue, true)
        return outValue.float
    }

fun VisibleRegion.contains(M: Point): Boolean {
    val AM = Point(
        M.latitude - topLeft.latitude,
        M.longitude - topLeft.longitude
    )
    val AB = Point(
        topRight.latitude - topLeft.latitude,
        topRight.longitude - topLeft.longitude
    )
    val AD = Point(
        bottomLeft.latitude - topLeft.latitude,
        bottomLeft.longitude - topLeft.longitude
    )
    val AMprodAB = AM.latitude * AB.latitude + AM.longitude * AB.longitude
    val ABprodAB = AB.latitude * AB.latitude + AB.longitude * AB.longitude
    val AMprodAD = AM.latitude * AD.latitude + AM.longitude * AD.longitude
    val ADprodAD = AD.latitude * AD.latitude + AD.longitude * AD.longitude

    return ((0 < AMprodAB) && (AMprodAB < ABprodAB)) &&
            ((0 < AMprodAD) && (AMprodAD < ADprodAD))
}

fun Map.moveIfNotGreater(point: Point){
    if(this.cameraPosition.zoom >= standartZoom){
        this.move(
            CameraPosition(
                Point(point.latitude,
                    point.longitude), this.cameraPosition.zoom, 0F, 0F),
            Animation(Animation.Type.SMOOTH, 1F),
            null
        )
        return
    }
    this.move(
        CameraPosition(
            Point(point.latitude,
                point.longitude), standartZoom, 0F, 0F),
        Animation(Animation.Type.SMOOTH, 1F),
        null
    )
}

fun Map.moveTo(point: Point, zoom: Float = standartZoom){
    this.move(
        CameraPosition(
            Point(point.latitude,
                point.longitude), zoom, 0F, 0F),
        Animation(Animation.Type.SMOOTH, 1F),
        null
    )
}
