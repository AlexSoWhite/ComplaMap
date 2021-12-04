package com.example.complamap.model

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.example.complamap.R
import com.google.firebase.firestore.GeoPoint
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.mapkit.map.VisibleRegion
import com.yandex.runtime.image.ImageProvider

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

fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
    observe(
        lifecycleOwner,
        object : Observer<T> {
            override fun onChanged(t: T?) {
                observer.onChanged(t)
                removeObserver(this)
            }
        }
    )
}

fun GeoPoint.toPoint(): Point {
    return Point(this.latitude, this.longitude)
}

fun Context.getBitmapFromVectorDrawable(drawableId: Int): Bitmap? {
    var drawable = ContextCompat.getDrawable(this, drawableId) ?: return null

    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
        drawable = DrawableCompat.wrap(drawable).mutate()
    }

    val bitmap = Bitmap.createBitmap(
        drawable.intrinsicWidth,
        drawable.intrinsicHeight,
        Bitmap.Config.ARGB_8888
    ) ?: return null
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)

    return bitmap
}

