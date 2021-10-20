package com.example.complamap

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.complamap.databinding.FragmentMapBinding
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.location.LocationListener
import com.yandex.mapkit.location.LocationStatus
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.mapview.MapView

class MapFragment : Fragment() {
    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!
    private lateinit var mapView: MapView
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
        inflater: LayoutInflater, container: ViewGroup?,
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
}