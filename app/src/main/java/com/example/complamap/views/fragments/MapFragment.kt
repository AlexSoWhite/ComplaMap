package com.example.complamap.views.fragments

import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.complamap.R
import com.example.complamap.databinding.FragmentMapBinding
import com.example.complamap.model.Complaint
import com.example.complamap.model.ComplaintManager
import com.example.complamap.model.PlacemarkVisitor
import com.example.complamap.model.getBitmapFromVectorDrawable
import com.example.complamap.model.moveIfNotGreater
import com.example.complamap.model.moveTo
import com.example.complamap.model.observeOnce
import com.example.complamap.model.toPoint
import com.example.complamap.viewmodel.MapViewModel
import com.example.complamap.views.activities.ComplaintActivity
import com.example.complamap.views.activities.CreateComplaintActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.layers.GeoObjectTapListener
import com.yandex.mapkit.logo.Alignment
import com.yandex.mapkit.logo.HorizontalAlignment
import com.yandex.mapkit.logo.VerticalAlignment
import com.yandex.mapkit.map.CameraListener
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.CameraUpdateReason
import com.yandex.mapkit.map.InputListener
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.map.MapObject
import com.yandex.mapkit.map.MapObjectTapListener
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.search.SearchFactory
import com.yandex.mapkit.search.SearchOptions
import com.yandex.mapkit.search.SearchType
import com.yandex.mapkit.search.search_layer.PlacemarkListener
import com.yandex.mapkit.search.search_layer.SearchLayer
import com.yandex.runtime.image.ImageProvider
import kotlinx.coroutines.launch

class MapFragment :
    Fragment(),
    MapObjectTapListener {
    private lateinit var binding: FragmentMapBinding
    private lateinit var mapView: MapView
    private lateinit var searchView: EditText
    lateinit var viewModel: MapViewModel
    private lateinit var searchLayer: SearchLayer
    private var currentPlacemark: PlacemarkMapObject? = null
    private var currentComplaint: PlacemarkMapObject? = null
    private val cameraListener = object : CameraListener {
        override fun onCameraPositionChanged(
            p0: Map,
            p1: CameraPosition,
            p2: CameraUpdateReason,
            p3: Boolean
        ) {
            p0.mapObjects.traverse(
                PlacemarkVisitor(
                    focusRegion = mapView.mapWindow.focusRegion,
                    zoom = p0.cameraPosition.zoom
                )
            )
        }
    }
    private val placemarkListener = PlacemarkListener { p0 ->
        viewModel.processResultItem(p0)
        true
    }
    private val inputListener = object : InputListener {
        override fun onMapTap(p0: Map, p1: Point) {
            mapView.map.deselectGeoObject()
            currentPlacemark = currentPlacemark?.let {
                mapView.map.mapObjects.remove(it)
                mapView.map.mapObjects.addPlacemark(p1, placemarkIcon)
            } ?: let {
                currentComplaint = currentComplaint?.let {
                    it.setIcon(placemarkIcon)
                    null
                }
                BottomSheetManager.visibleBottomSheet = R.id.map_object_info
                mapView.map.mapObjects.addPlacemark(p1, placemarkIcon)
            }
            viewModel.addressFromPoint(
                p1,
                mapView.map.cameraPosition.zoom.toInt()
            )
        }

        override fun onMapLongTap(p0: Map, p1: Point) {
            // Функционала для длинного нажатия на карту в проекте нет
        }
    }
    private val geoObjectTapListener = GeoObjectTapListener { p0 ->
        viewModel.apply {
            processSelectedObject(p0)
            addressFromPoint(
                p0.geoObject.geometry[0].point!!,
                mapView.map.maxZoom.toInt()
            )
        }
        currentComplaint = currentComplaint?.let {
            it.setIcon(placemarkIcon)
            null
        }
        currentPlacemark = currentPlacemark?.let { mapView.map.mapObjects.remove(it); null }
        BottomSheetManager.visibleBottomSheet = R.id.map_object_info
        currentPlacemark = mapView.map.mapObjects.addPlacemark(
            p0.geoObject.geometry[0].point!!,
            placemarkIcon
        )
        true
    }
    private lateinit var placemarkIcon: ImageProvider
    private lateinit var placemarkPickedIcon: ImageProvider
    companion object {
        private val defaultMapPosition = Point(55.751574, 37.573856)
        private const val defaultMapZoom = 11F
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMapBinding.inflate(inflater, container, false)
        val view = binding.root
        placemarkIcon = ImageProvider.fromBitmap(
            requireContext().getBitmapFromVectorDrawable(
                R.drawable.ic_placemark
            )
        )
        placemarkPickedIcon = ImageProvider.fromBitmap(
            requireContext().getBitmapFromVectorDrawable(
                R.drawable.ic_placemark_picked
            )
        )
        BottomSheetManager.binding = binding
        initializeFragment()
        initializeViewsListeners()
        return view
    }

    override fun onMapObjectTap(p0: MapObject, p1: Point): Boolean {
        val data = p0.userData as Complaint
        currentComplaint?.setIcon(placemarkIcon)
        currentComplaint = (p0 as PlacemarkMapObject).apply {
            setIcon(placemarkPickedIcon)
        }
        mapView.map.moveIfNotGreater(
            Point(
                currentComplaint!!.geometry.latitude,
                currentComplaint!!.geometry.longitude
            )
        )
        viewModel.loadPhoto(
            binding.infoC.complaint.image.context,
            data,
            binding.infoC.complaint.image
        )
        ComplaintManager.setComplaint(data)
        currentPlacemark = currentPlacemark?.let {
            mapView.map.mapObjects.remove(it)
            mapView.map.deselectGeoObject()
            null
        }
        binding.infoC.complaint.complaint = data
        BottomSheetManager.visibleBottomSheet = R.id.bottom_sheet_parent
        BottomSheetManager.visibleBottomSheet = R.id.complaint_info
        return true
    }

    override fun onResume() {
        super.onResume()
        if (ComplaintManager.justPublished) {
            val data = ComplaintManager.getCurrentComplaint()!!
            currentPlacemark = currentPlacemark?.let {
                viewModel.loadPhoto(
                    binding.infoC.complaint.image.context,
                    data,
                    binding.infoC.complaint.image
                )
                currentComplaint = mapView.map.mapObjects.addPlacemark(
                    it.geometry,
                    placemarkPickedIcon
                ).apply {
                    userData = data
                    isVisible = true
                    addTapListener(this@MapFragment)
                }
                mapView.map.mapObjects.remove(it)
                null
            }
            mapView.map.moveIfNotGreater(
                Point(
                    currentComplaint!!.geometry.latitude,
                    currentComplaint!!.geometry.longitude
                )
            )
            BottomSheetManager.visibleBottomSheet = R.id.complaint_info
            binding.infoC.complaint.complaint = ComplaintManager.getCurrentComplaint()!!
        }
        if (currentComplaint != null) {
            ComplaintManager.getCurrentComplaint()?.let {
                currentComplaint!!.userData = it
                binding.infoC.complaint.complaint = it
            } ?: let {
                mapView.map.mapObjects.remove(currentComplaint!!)
                BottomSheetManager.toStandardSheet()
                currentComplaint = null
            }
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.apply {
            address.observe(viewLifecycleOwner) {
                binding.bottomSheetParent.addressView.text = it
                binding.info.fullAddress.text = it
            }
            coordinates.observe(viewLifecycleOwner) {
                binding.bottomSheetParent.coordinatesView.text = it
                binding.info.coordinatesInfo.text = it
            }
            selectionMetadata.observe(viewLifecycleOwner) {
                mapView.map.selectGeoObject(it.id, it.layerId)
            }
            shortAddress.observe(viewLifecycleOwner) {
                binding.info.shortAddress.text = it
            }
            complaintsList.observeOnce(viewLifecycleOwner) { list ->
                viewLifecycleOwner.lifecycleScope.launch {
                    list.forEach { complaint ->
                        mapView.map.mapObjects.addPlacemark(
                            complaint.location!!.toPoint(),
                            placemarkIcon
                        ).apply {
                            userData = complaint
                            isVisible = false
                            addTapListener(this@MapFragment)
                        }
                    }
                }
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
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
    private fun initializeFragment() {
        mapView = binding.mapview
        viewModel = ViewModelProvider(this)[MapViewModel::class.java]
        mapView.map.moveTo(defaultMapPosition, defaultMapZoom)
        setBottomSheetPeekHeight(binding.bottomSheetParent.root)
        mapView.map.logo.apply {
            this.setAlignment(Alignment(HorizontalAlignment.RIGHT, VerticalAlignment.TOP))
        }
        binding.infoC.complaint.listItemSeparator.visibility = View.GONE
        searchView = binding.bottomSheetParent.searchView
        searchLayer = SearchFactory.getInstance().createSearchLayer(mapView.mapWindow)
    }
    private fun initializeViewsListeners() {
        searchView.setOnEditorActionListener { textView, actionId, keyEvent ->
            val searchType = SearchType.GEO.value or SearchType.BIZ.value
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                if (searchView.text.toString().isNotBlank() &&
                    searchView.text.toString().isNotEmpty()
                ) {
                    searchLayer.submitQuery(
                        searchView.text.toString(),
                        SearchOptions().setSearchTypes(searchType)
                    )
                } else searchLayer.clear()
            }; false
        }
        binding.fab.setOnClickListener {
            binding.bottomSheetParent.addressView.text.let {
                if (it.isNotEmpty() && it.isNotBlank() &&
                    BottomSheetManager.visibleBottomSheet == R.id.map_object_info
                ) {
                    val intent = Intent(requireContext(), CreateComplaintActivity::class.java)
                    intent.apply {
                        putExtra(AddPlacemarkDialog.EXTRA_ADDRESS, it)
                        binding.bottomSheetParent.coordinatesView.text.split(" ").let {
                            putExtra(
                                AddPlacemarkDialog.EXTRA_LATITUDE,
                                currentPlacemark!!.geometry.latitude
                            )
                            putExtra(
                                AddPlacemarkDialog.EXTRA_LONGITUDE,
                                currentPlacemark!!.geometry.longitude
                            )
                        }
                    }; startActivity(intent)
                } else {
                    Toast.makeText(requireContext(), "Поставьте метку", Toast.LENGTH_SHORT).show()
                }
            }
        }
        binding.info.closeButton.setOnClickListener {
            currentPlacemark?.let { mapView.map.mapObjects.remove(it) }
            mapView.map.deselectGeoObject()
            currentPlacemark = null
            BottomSheetManager.toStandardSheet()
        }
        binding.infoC.closeButton.setOnClickListener {
            BottomSheetManager.toStandardSheet()
            currentComplaint?.setIcon(placemarkIcon)
        }
        binding.infoC.mapInfoCard.setOnClickListener {
            val intent = Intent(context, ComplaintActivity::class.java)
            ComplaintManager.setComplaint(binding.infoC.complaint.complaint)
            intent.putExtra("FragmentMode", "View")
            startActivity(intent)
        }
        mapView.map.addTapListener(geoObjectTapListener)
        mapView.map.addInputListener(inputListener)
        searchLayer.addPlacemarkListener(placemarkListener)
        mapView.map.addCameraListener(cameraListener)
    }
    private fun setBottomSheetPeekHeight(view: View) {
        val bottomSheetBehavior = BottomSheetBehavior.from(view)
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
