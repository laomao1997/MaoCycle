package io.github.laomao1997.maocycle.fragments

import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import io.github.laomao1997.maocycle.MainViewModel
import io.github.laomao1997.maocycle.R

class MapFragment : Fragment() {

    private lateinit var mViewModel: MainViewModel
    private lateinit var mMapView: MapView
    private var mMapboxMap: MapboxMap? = null
    private var mCurrentLocation: Location? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_map, container)
        mMapView = view.findViewById(R.id.map_view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        connectViewModel()
        mMapView.onCreate(savedInstanceState)
        mMapView.getMapAsync { mapboxMap ->
            this.mMapboxMap = mapboxMap
            this.mMapboxMap?.setStyle(Style.OUTDOORS)
            if (mViewModel.latitude.value != null && mViewModel.longitude.value != null) {
                updateMapCamera(mViewModel.latitude.value!!, mViewModel.longitude.value!!)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        mMapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mMapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mMapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        mMapView.onStop()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mMapView.onSaveInstanceState(outState)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mMapView.onLowMemory()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mMapView.onDestroy()
    }

    private fun connectViewModel() {
        mViewModel = ViewModelProvider(activity!!).get(MainViewModel::class.java)
        mViewModel.currentLocation.observe(this) {
            mCurrentLocation = it
            updateMapCamera(mCurrentLocation!!.latitude, mCurrentLocation!!.longitude)
        }
        mViewModel.distance.observe(this) {
//            updateDistanceText(it)
        }
    }

    private fun updateMapCamera(mapLatitude: Double, mapLongitude: Double) {
        val position: CameraPosition = CameraPosition.Builder()
            .target(LatLng(mapLatitude, mapLongitude)) // Sets the new camera position
            .zoom(15.0) // Sets the zoom
            .bearing(0.0) // Rotate the camera
            .tilt(0.0) // Set the camera tilt
            .build() // Creates a CameraPosition from the builder
        mMapboxMap?.animateCamera(
            CameraUpdateFactory
                .newCameraPosition(position), 1000
        )

    }
}