package com.example.complamap.model

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.google.firebase.firestore.GeoPoint
import com.yandex.mapkit.geometry.Point

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
    var drawable = ContextCompat.getDrawable(this, drawableId)

    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
        drawable = drawable?.let {
            DrawableCompat.wrap(it).mutate()
        }
    }

    val bitmap = drawable?.let {
        Bitmap.createBitmap(
            it.intrinsicWidth,
            it.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
    }
    val canvas = bitmap?.let {
        Canvas(it)
    }
    canvas?.let {
        drawable?.setBounds(0, 0, it.width, it.height)
        drawable?.draw(it)
    }

    return bitmap
}
