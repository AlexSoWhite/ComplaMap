package com.example.complamap

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.example.complamap.databinding.FragmentMapBinding
import com.example.complamap.databinding.PlacemarkViewBinding
import com.yandex.mapkit.Animation
import com.yandex.mapkit.GeoObject
import com.yandex.mapkit.MapKit
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.location.LocationListener
import com.yandex.mapkit.location.LocationStatus
import com.yandex.mapkit.logo.Alignment
import com.yandex.mapkit.logo.HorizontalAlignment
import com.yandex.mapkit.logo.VerticalAlignment
import com.yandex.mapkit.map.*
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.ui_view.ViewProvider

class MapFragment() : Fragment() {
    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!
    private lateinit var mapView: MapView
    private lateinit var placemark: PlacemarkMapObject
    private val locationListener: com.yandex.mapkit.location.LocationListener = object :
        LocationListener {
        override fun onLocationUpdated(p0: com.yandex.mapkit.location.Location) {
            mapView.map.move(
                CameraPosition(p0.position, 14F, 0F, 0F),
                Animation(Animation.Type.SMOOTH, 1F),
                null
            )
        }

        override fun onLocationStatusUpdated(p0: LocationStatus) {}
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        val view = binding.root
        mapView = binding.mapview

        mapView.map.move(
            CameraPosition(Point(55.751574, 37.573856), 11F, 0F, 0F),
            Animation(Animation.Type.SMOOTH, 0F),
            null
        )
        GeoObject()
        mapView.map.addInputListener(
                object : InputListener{
                    override fun onMapTap(p0: Map, p1: Point) {
                        placemark.geometry = p1
                    }

                    override fun onMapLongTap(p0: Map, p1: Point) { }

                }
        )

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val textView = TextView(activity)
        val colors = intArrayOf(Color.RED, Color.GREEN, Color.BLACK)
        val params = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        textView.apply {
            layoutParams = params
            text = "Hello"

        }

        placemark = mapView.map.mapObjects.addPlacemark(Point(55.751574, 37.573856), ViewProvider(textView))
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
}