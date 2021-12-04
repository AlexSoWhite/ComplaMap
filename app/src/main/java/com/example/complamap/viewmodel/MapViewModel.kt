package com.example.complamap.viewmodel

import android.content.Context
import android.widget.ImageView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.example.complamap.R
import com.example.complamap.model.Complaint
import com.example.complamap.model.MapRepository
import com.example.complamap.model.OnAddressFetchedListener
import com.example.complamap.model.OnGeoObjectFetchedListener
import com.example.complamap.model.PointAddressConverter
import com.yandex.mapkit.GeoObject
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.layers.GeoObjectTapEvent
import com.yandex.mapkit.map.GeoObjectSelectionMetadata
import com.yandex.mapkit.search.Address
import com.yandex.mapkit.search.BusinessObjectMetadata
import com.yandex.mapkit.search.SearchType
import com.yandex.mapkit.search.ToponymObjectMetadata
import com.yandex.mapkit.search.search_layer.SearchResultItem
import kotlinx.coroutines.launch

class MapViewModel : ViewModel() {
    private val addressMutable = MutableLiveData<String>()
    val address: LiveData<String> get() = addressMutable
    private val coordinatesMutable: MutableLiveData<String> = MutableLiveData()
    val coordinates: LiveData<String> = coordinatesMutable
    private val selectionMetadataMutable = MutableLiveData<GeoObjectSelectionMetadata>()
    val selectionMetadata: LiveData<GeoObjectSelectionMetadata>
        get() = selectionMetadataMutable
    private val complaintsListMutable: MutableLiveData<List<Complaint>> = MutableLiveData()
    val complaintsList: LiveData<List<Complaint>> = complaintsListMutable

    private val pointAddressConverter = PointAddressConverter(searchType = SearchType.GEO.value)
        .apply {
            addOnAddressFetchedListener(
                object : OnAddressFetchedListener {
                    override fun onSuccess(address: String?) {
                        address?.let { updateAddress(it) }
                    }
                }
            )
            addonGeoObjectFetchedListener(
                object : OnGeoObjectFetchedListener {
                    override fun onSuccess(obj: GeoObject?) {
                        obj?.geometry?.get(0)?.point?.let { updateCoordinates(it) }
                    }
                }
            )
        }

    init {
        viewModelScope.launch {
            MapRepository.getComplaintWithLocation {
                complaintsListMutable.value = it
            }
        }
    }

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
        coordinatesMutable.value = "${point.latitude} ${point.longitude}"
    }
    private fun updateAddress(address: String) {
        addressMutable.value = address
    }

    fun processResultItem(item: SearchResultItem) {
        PointAddressConverter.fetchAddress(item.geoObject)?.let { updateAddress(it) }
        updateCoordinates(item.point)
    }

    fun loadPhoto(context: Context, complaint: Complaint, container: ImageView) {
        viewModelScope.launch {
            Glide.with(context)
                .load(complaint.photo)
                .placeholder(R.drawable.default_placeholder)
                .into(container)
        }
    }
    fun addressFromPoint(p: Point, zoom: Int) {
        pointAddressConverter.addressFromPoint(p, zoom)
    }

    override fun onCleared() {
        super.onCleared()
        pointAddressConverter.removeGeoObjectFetchedListener()
        pointAddressConverter.removeOnAddressFetchedListener()
    }
}
