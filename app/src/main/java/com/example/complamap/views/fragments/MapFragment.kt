package com.example.complamap.views.fragments

import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.complamap.R
import com.example.complamap.databinding.FragmentMapBinding
import com.example.complamap.viewmodel.MapViewModel
import com.example.complamap.views.activities.CreateComplaintActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior
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
import com.yandex.mapkit.search.SearchFactory
import com.yandex.mapkit.search.SearchOptions
import com.yandex.mapkit.search.SearchType
import com.yandex.mapkit.search.search_layer.PlacemarkListener
import com.yandex.mapkit.search.search_layer.SearchLayer
import com.yandex.mapkit.search.search_layer.SearchResultItem

class MapFragment() : Fragment(), GeoObjectTapListener, InputListener, PlacemarkListener {
    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!
    private lateinit var mapView: MapView
    private var isGeoObjectSelected: Boolean = false
    private lateinit var searchView: EditText
    private lateinit var viewModel: MapViewModel
    private lateinit var searchLayer: SearchLayer
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentMapBinding.inflate(inflater, container, false)
        val view = binding.root
        mapView = binding.mapview
        viewModel = ViewModelProvider(this)[MapViewModel::class.java]
        mapView.map.move(
            CameraPosition(Point(55.751574, 37.573856), 11F, 0F, 0F),
            Animation(Animation.Type.SMOOTH, 0F),
            null
        )
        setBottomSheetPeekHeight()
        binding.fab.setOnClickListener {
            val intent = Intent(requireContext(), CreateComplaintActivity::class.java)
            startActivity(intent)
        }
        mapView.map.logo.apply {
            this.setAlignment(Alignment(HorizontalAlignment.RIGHT, VerticalAlignment.TOP))
        }
        mapView.map.addTapListener(this)
        mapView.map.addInputListener(this)
        searchView = binding.bottomSheetParent.searchView
        searchView.setOnEditorActionListener(
            object : TextView.OnEditorActionListener {
                override fun onEditorAction(
                    textView: TextView?,
                    actionId: Int,
                    keyEvent: KeyEvent?
                ): Boolean {
                    val searchType = SearchType.GEO.value or SearchType.BIZ.value
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        if (searchView.text.toString().isNotBlank() && searchView.text.toString().isNotEmpty()) {
                            searchLayer.submitQuery(
                                searchView.text.toString(),
                                SearchOptions().setSearchTypes(searchType)
                            )
                        } else
                            searchLayer.clear()
                    }
                    return false
                }
            }
        )
        searchLayer = SearchFactory.getInstance().createSearchLayer(mapView.mapWindow)
        searchLayer.addPlacemarkListener(this)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.apply {
            address.observe(viewLifecycleOwner) {
                binding.bottomSheetParent.addressView.text = it
            }
            coordinates.observe(viewLifecycleOwner) {
                binding.bottomSheetParent.coordinatesView.text = it
            }
            selectionMetadata.observe(viewLifecycleOwner) {
                mapView.map.selectGeoObject(it.id, it.layerId)
            }
        }
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

    override fun onMapTap(p0: Map, p1: Point) {
        viewModel.addressFromPoint(
            p1,
            mapView.map.cameraPosition.zoom.toInt()
        )
    }

    override fun onMapLongTap(p0: Map, p1: Point) {}

    override fun onTap(p0: SearchResultItem): Boolean {
        viewModel.processResultItem(p0)
        return true
    }

    override fun onObjectTap(p0: GeoObjectTapEvent): Boolean {
        viewModel.apply {
            processSelectedObject(p0)
            addressFromPoint(
                p0.geoObject.geometry[0].point!!,
                mapView.map.maxZoom.toInt()
            )
        }
        return true
    }

    private fun setBottomSheetPeekHeight() {
        val bottomSheetParent = binding.bottomSheetParent.root
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetParent)
        val tv = TypedValue()
        if (requireActivity().theme.resolveAttribute(R.attr.actionBarSize, tv, true)) {
            val actionBarHeight = TypedValue.complexToDimensionPixelSize(
                tv.data,
                resources.displayMetrics
            )
            bottomSheetBehavior.peekHeight = actionBarHeight * 2
        }
    }
}
