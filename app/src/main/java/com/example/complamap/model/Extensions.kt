package com.example.complamap.model

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.google.firebase.firestore.GeoPoint
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.VisibleRegion

fun VisibleRegion.contains(M: Point): Boolean{
    val AM = Point(
        M.latitude - topLeft.latitude,
        M.longitude - topLeft.longitude)
    val AB = Point(
        topRight.latitude - topLeft.latitude,
        topRight.longitude - topLeft.longitude)
    val AD = Point(
        bottomLeft.latitude - topLeft.latitude,
        bottomLeft.longitude - topLeft.longitude)
    val AMprodAB = AM.latitude * AB.latitude + AM.longitude * AB.longitude
    val ABprodAB = AB.latitude * AB.latitude + AB.longitude * AB.longitude
    val AMprodAD = AM.latitude * AD.latitude + AM.longitude * AD.longitude
    val ADprodAD = AD.latitude * AD.latitude + AD.longitude * AD.longitude

    return ((0 < AMprodAB) && (AMprodAB < ABprodAB)) &&
            ((0 < AMprodAD) && (AMprodAD < ADprodAD))
}

fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
    observe(lifecycleOwner, object : Observer<T> {
        override fun onChanged(t: T?) {
            observer.onChanged(t)
            removeObserver(this)
        }
    })
}

fun GeoPoint.toPoint(): Point{
    return Point(this.latitude, this.longitude)
}