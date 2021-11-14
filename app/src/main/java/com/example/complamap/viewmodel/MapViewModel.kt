package com.example.complamap.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.complamap.model.PointAddressConverter
import com.yandex.mapkit.GeoObject
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.layers.GeoObjectTapEvent
import com.yandex.mapkit.map.GeoObjectSelectionMetadata
import com.yandex.mapkit.search.*
import com.yandex.mapkit.search.search_layer.SearchResultItem
import com.yandex.runtime.Error

class MapViewModel : ViewModel() {
    private val addressMutable: MutableLiveData<String> = MutableLiveData()
    val address: LiveData<String> get() = addressMutable
    private val coordinatesMutable: MutableLiveData<String> = MutableLiveData()
    val coordinates: LiveData<String> = coordinatesMutable
    private val selectionMetadataMutable: MutableLiveData<GeoObjectSelectionMetadata> = MutableLiveData()
    val selectionMetadata: LiveData<GeoObjectSelectionMetadata> get() = selectionMetadataMutable
    private val pointAddressConverter = PointAddressConverter(
        object : Session.SearchListener {
            override fun onSearchResponse(p0: Response) {
                p0.collection.children.firstOrNull()?.let { it ->
                    fetchAddress(it.obj!!)?.let { updateAddress(it) }
                    updateCoordinates(it.obj!!.geometry[0].point!!)
                }
            }

            override fun onSearchError(p0: Error) {
                Log.e("onSearchError", p0.toString())
            }
        },
        searchType = SearchType.GEO.value
    )

    fun processSelectedObject(p0: GeoObjectTapEvent) {
        val selectionMetadata = p0
            .geoObject
            .metadataContainer
            .getItem(GeoObjectSelectionMetadata::class.java)
        selectionMetadata?.let {
            selectionMetadataMutable.value = it
        }
    }

    private fun updateCoordinates(point: Point) {
        coordinatesMutable.value = "Kоординаты: ${point.latitude} ${point.longitude}"
    }
    private fun updateAddress(address: String) {
        addressMutable.value = "Адрес: $address"
    }

    fun processResultItem(item: SearchResultItem) {
        fetchAddress(item.geoObject)?.let { updateAddress(it) }
        updateCoordinates(item.point)
    }

    private fun fetchAddress(item: GeoObject): String? =
        item.metadataContainer
            .getItem(ToponymObjectMetadata::class.java)
            ?.address?.formattedAddress
            ?: let {
            item.metadataContainer
                .getItem(
                    BusinessObjectMetadata::class.java
                )?.address?.formattedAddress
        }

    fun addressFromPoint(p: Point, zoom: Int) {
        pointAddressConverter.addressFromPoint(p, zoom)
    }
}
