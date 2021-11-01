package com.example.complamap

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.complamap.databinding.FragmentMapBinding
import com.example.complamap.databinding.ItemBottomSheetContainerBinding
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.layers.GeoObjectTapEvent
import com.yandex.mapkit.layers.GeoObjectTapListener
import com.yandex.mapkit.logo.Alignment
import com.yandex.mapkit.logo.HorizontalAlignment
import com.yandex.mapkit.logo.VerticalAlignment
import com.yandex.mapkit.map.*
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.mapview.MapView

class MapFragment() : Fragment(), GeoObjectTapListener, InputListener {
    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!
    private lateinit var mapView: MapView
    private lateinit var placemark: PlacemarkMapObject
    private var isGeoObjectSelected:Boolean = false
    private var _itemBottomSheetContainerBinding: ItemBottomSheetContainerBinding? = null
    private val itemBottomSheetContainerBinding get() = _itemBottomSheetContainerBinding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        _itemBottomSheetContainerBinding = ItemBottomSheetContainerBinding.inflate(layoutInflater)
        val view = binding.root
        mapView = binding.mapview
        mapView.map.move(
            CameraPosition(Point(55.751574, 37.573856), 11F, 0F, 0F),
            Animation(Animation.Type.SMOOTH, 0F),
            null
        )
        mapView.map.logo.apply {
            this.setAlignment(Alignment(HorizontalAlignment.RIGHT, VerticalAlignment.TOP))
        }
        mapView.map.addTapListener(this)
        mapView.map.addInputListener(this)
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
        MapKitFactory.getInstance().onStop()
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
        MapKitFactory.getInstance().onStart()
    }

    override fun onObjectTap(p0: GeoObjectTapEvent): Boolean {
        val selectionMetadata = p0
            .geoObject
            .metadataContainer
            .getItem(GeoObjectSelectionMetadata::class.java)
        val geoObjectPoint = p0.geoObject.geometry[0].point
        Log.e("onObjectTap"," latitude = ${geoObjectPoint?.latitude} longitude = ${geoObjectPoint?.longitude}")
        selectionMetadata?.let {
            mapView.map.selectGeoObject(selectionMetadata.id, selectionMetadata.layerId)
        }
        return selectionMetadata?.let {
            isGeoObjectSelected = true
            mapView.map.mapObjects.addPlacemark(geoObjectPoint!!)
            setCoordinates(geoObjectPoint)
            true
        }?:false
    }

    override fun onMapTap(p0: Map, p1: Point) {
        if(isGeoObjectSelected){
            mapView.map.deselectGeoObject()
            isGeoObjectSelected = false
        }else{
            mapView.map.mapObjects.addPlacemark(p1)
            setCoordinates(p1)
        }

    }

    override fun onMapLongTap(p0: Map, p1: Point) {
    }

    private fun setCoordinates(p1: Point){
        itemBottomSheetContainerBinding.coordinatesView.text = "Координаты:  ${p1.longitude}, ${p1.latitude}"
        val coor = activity?.findViewById<TextView>(R.id.coordinates_view)
        coor?.text = "Координаты:  ${p1.longitude}, ${p1.latitude}"
    }


}