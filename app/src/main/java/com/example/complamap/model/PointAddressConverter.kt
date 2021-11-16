package com.example.complamap.model

import android.util.Log
import com.yandex.mapkit.GeoObject
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.search.*
import com.yandex.runtime.Error

class PointAddressConverter(
    searchType: Int
) { // На момент создания SearchFactory должна быть инициализирована
    private val searchType: Int = searchType // Либо GEO(топоним), либо BIZ(организация)
    private var searchSession: Session? = null
    private val searchListener: Session.SearchListener = object : Session.SearchListener {
        override fun onSearchResponse(p0: Response) {
            p0.collection.children.firstOrNull()?.let { it ->
                onGeoObjectListener?.onSuccess(it.obj)
                onAddressListener?.onSuccess(fetchAddress(it.obj!!))
            }
        }

        override fun onSearchError(p0: Error) {
            onAddressListener?.onError(p0)
            onGeoObjectListener?.onError(p0)
            Log.e("onSearchError", p0.toString())
        }
    }
    private var onAddressListener: OnAddressFetchedListener? = null
    private var onGeoObjectListener: OnGeoObjectFetchedListener? = null

    private val searchManager: SearchManager = SearchFactory
        .getInstance()
        .createSearchManager(SearchManagerType.ONLINE)

    fun addressFromPoint(p: Point, zoom: Int) { // От zoom зависит избыточность адреса
        searchSession = searchManager
            .submit(
                p,
                zoom,
                SearchOptions().setSearchTypes(searchType),
                searchListener
            )
    }

    fun addOnAddressFetchedListener(listener: OnAddressFetchedListener): Boolean {
        onAddressListener = onAddressListener?.let { return false } ?: listener
        return true
    }
    fun removeOnAddressFetchedListener() {
        onAddressListener = null
    }
    fun addonGeoObjectFetchedListener(listener: OnGeoObjectFetchedListener): Boolean {
        onGeoObjectListener = onGeoObjectListener?.let { return false } ?: listener
        return true
    }
    fun removeGeoObjectFetchedListener() {
        onGeoObjectListener = null
    }
    companion object {
        fun fetchAddress(item: GeoObject): String? =
            item.metadataContainer
                .getItem(ToponymObjectMetadata::class.java)
                ?.address?.formattedAddress
                ?: let {
                    item.metadataContainer
                        .getItem(
                            BusinessObjectMetadata::class.java
                        )?.address?.formattedAddress
                }
    }
}
