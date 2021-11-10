package com.example.skismoring.ui.map

import android.content.Context
import android.content.res.Resources
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.RectF
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ThemedSpinnerAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.example.skismoring.R
import com.example.skismoring.ui.location.LocationViewModel
import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.expressions.Expression
import com.mapbox.mapboxsdk.style.layers.PropertyFactory.*
import com.mapbox.mapboxsdk.style.layers.SymbolLayer
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource


class MapFragment : Fragment(), OnMapReadyCallback, MapboxMap.OnMapClickListener  {
    private lateinit var mapView: MapView
    private lateinit var mapboxMap: MapboxMap
    private lateinit var mapViewModel : MapViewModel
    private lateinit var locationViewModel : LocationViewModel

    private val geoJsonLocationSourceId = "geoJsonLocations"
    private val geoJsonLocationLayerId = "locations"
    private val locationIconId = "locationsIcon"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mapViewModel = ViewModelProvider(requireActivity()).get(MapViewModel::class.java)
        locationViewModel = ViewModelProvider(requireActivity()).get(LocationViewModel::class.java)
        mapViewModel.readData(requireContext())

        // Access mapbox with token
        context?.applicationContext?.let { Mapbox.getInstance(
                it, getString(R.string.mapbox_access_token)
        ) }
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {

        // Inflate view
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // MapView
        mapView = view.findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        // Search
        val searchText = view.findViewById<EditText>(R.id.mapSearchView)
        searchText.setOnClickListener {
            findNavController().navigate(
                    R.id.mapToSearch,
                    null,
                    navOptions {
                        anim {
                            enter = R.anim.invis_to_vis
                            exit = R.anim.no_change_out
                        }
                    }
            )
        }

        // Settings
        val settingsButton: ImageButton = view.findViewById(R.id.settingsButton)
        settingsButton.setOnClickListener {
            findNavController().navigate(
                    R.id.mapToSettings,
                    null,
                    navOptions {
                        anim {
                            enter = R.anim.invis_to_vis
                            exit = R.anim.no_change_out
                        }
                    }
            )
        }
    }

    override fun onMapReady(readyMapboxMap: MapboxMap) {
        mapboxMap = readyMapboxMap

        // Facilitate styling based on chosen theme
        val sharedPref = requireActivity().getSharedPreferences("Theme", Context.MODE_PRIVATE)
        var currentStyleUri = "<INSERT-MAPBOX-STYLE-URI>"
        val currentStyle = sharedPref.getInt("Theme", R.style.Theme_Skismoring_Light)
        var locationTextColor = Color.BLACK
        var locationTextHaloColor = Color.WHITE
        var locationTextHaloWidth = 0.8F

        when(currentStyle){
            R.style.Theme_Skismoring_Light -> {
                currentStyleUri = "<INSERT-MAPBOX-STYLE-URI>"
                locationTextColor = Color.BLACK
                locationTextHaloColor = Color.WHITE
                locationTextHaloWidth = 0.5F
            }
            R.style.Theme_Skismoring_Dark -> {
                currentStyleUri = "<INSERT-MAPBOX-STYLE-URI>"
                locationTextColor = Color.WHITE
                locationTextHaloColor = Color.TRANSPARENT
            }
            R.style.Theme_Skismoring_Contrast -> {
                currentStyleUri = "<INSERT-MAPBOX-STYLE-URI>"
                locationTextColor = Color.BLACK
                locationTextHaloColor = Color.YELLOW
                locationTextHaloWidth = 2F
            }
        }


        mapboxMap.setStyle(Style.Builder().fromUri(currentStyleUri)) { it ->
            // Map is set up and the style has loaded. Now you can add data or make other map adjustments.

            mapboxMap.uiSettings.isRotateGesturesEnabled = false
            mapboxMap.addOnMapClickListener(this)

            // Add the location targets in a symbollayer
            it.addImage(locationIconId, BitmapFactory.decodeResource(resources, R.drawable.mapbox_marker_icon_default))
            val fc = FeatureCollection.fromJson(
                    requireContext().assets.open("locations.geojson").bufferedReader().use { it.readText() }
            )
            val source = GeoJsonSource(geoJsonLocationSourceId, fc)
            it.addSource(source)
            val layer = SymbolLayer(geoJsonLocationLayerId, geoJsonLocationSourceId).withProperties(
                    iconImage(locationIconId),
                    iconAllowOverlap(false),
                    iconIgnorePlacement(false),
                    textField(Expression.get("name")),
                    textOffset(arrayOf(0.0f, 0.5f)),
                    textColor(locationTextColor),
                    textHaloWidth(locationTextHaloWidth),
                    textHaloColor(locationTextHaloColor)
            )
            it.addLayer(layer)

            //Animate to position of location
            if(view != null){
                mapViewModel.mapPosition.observe(viewLifecycleOwner, {
                    val position = CameraPosition.Builder()
                            .target(LatLng(it.lat, it.lng))
                            .zoom(it.zoom)
                            .build()

                    mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), 1500)
                })
            }
        }
    }

    /* Handles on mapclick by changing saved pos in map, and also setting current location to
    * be shown in LocationView*/
    override fun onMapClick(point: LatLng): Boolean {
        val pointF = mapboxMap.projection.toScreenLocation(point)
        val rectF = RectF(pointF.x - 10, pointF.y - 10, pointF.x + 10, pointF.y + 10)
        val featureList: List<Feature> = mapboxMap.queryRenderedFeatures(rectF, geoJsonLocationLayerId)
        if (featureList.isNotEmpty()) {
            mapViewModel.changePos(Position(point.latitude, point.longitude, 12.0))
            locationViewModel.setCurrentLocationFromFeature(featureList[0])
            findNavController().navigate(
                    R.id.mapToLocation,
                    null,
                    navOptions {
                        anim {
                            enter = R.anim.invis_to_vis
                            exit = R.anim.no_change_out
                        }
                    }
            )
            return true
        }
        return false
    }

    // Add the mapView lifecycle to the activity's lifecycle methods
    override fun onResume() {
        super.onResume()
        if (this::mapView.isInitialized) mapView.onResume()
    }

    override fun onStart() {
        super.onStart()
        if (this::mapView.isInitialized) mapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        if (this::mapView.isInitialized) mapView.onStop()
    }

    override fun onPause() {
        super.onPause()
        if (this::mapView.isInitialized) mapView.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        if (this::mapView.isInitialized) mapView.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (this::mapboxMap.isInitialized) mapboxMap.removeOnMapClickListener(this)
        if (this::mapView.isInitialized) mapView.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (this::mapView.isInitialized) mapView.onSaveInstanceState(outState)
    }
}

