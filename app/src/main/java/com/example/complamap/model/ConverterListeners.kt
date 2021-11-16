package com.example.complamap.model

import com.yandex.mapkit.GeoObject
import com.yandex.runtime.Error
interface OnAddressFetchedListener {

    fun onSuccess(address: String?)

    fun onError(error: Error) {}
}
interface OnGeoObjectFetchedListener {

    fun onSuccess(obj: GeoObject?)

    fun onError(error: Error) {}
}
