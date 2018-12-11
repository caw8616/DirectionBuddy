package com.example.catherine.directionbuddy

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import android.location.LocationManager
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import android.widget.Toast
import com.example.catherine.directionbuddy.entities.Direction
import com.example.catherine.directionbuddy.viewmodels.AllDirectionsViewModel
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.location.places.ui.PlacePicker
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import java.io.IOException



/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [MapFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [MapFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class MapFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

//    private var listener: OnFragmentInteractionListener? = null
    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var lastLocation: Location
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest
    private var locationUpdateState = false
    var directionsViewModel: AllDirectionsViewModel? = null
    private var userId: String = ""
    private var username: String = ""
    var markerList: List<MarkerOptions>?= null


    companion object {
        const val LOCATION_PERMISSION_REQUEST_CODE = 1
        const val REQUEST_CHECK_SETTINGS = 2
        const val PLACE_PICKER_REQUEST = 3
        @JvmStatic
        fun newInstance(_userId: String, _username :String ) =
                MapFragment().apply {
                    arguments = Bundle().apply {
//                        putInt(USER_ID, userId)
//                        putString(USERNAME, username)
                        userId = _userId
                        username = _username
                    }
                }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
//        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity as Activity)

        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity as Activity)

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)

                lastLocation = p0.lastLocation
//                placeMarkerOnMap(LatLng(lastLocation.latitude, lastLocation.longitude))
            }
        }
        createLocationRequest()
        val fab = view.findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            loadPlacePicker()
        }
        directionsViewModel = AllDirectionsViewModel(activity?.application!!, userId!!)
        directionsViewModel!!.getAllDirections().observe(this, Observer {
//            mAdapter!!.addAll(it ?: emptyList())
            it!!.forEach {
               var address = getLatLong(it)
                if(address != null) {
                    val currentLatLng = LatLng(address.latitude, address.longitude)
                    placeDirectionMarkerOnMap(currentLatLng, it.name)
                }

            }
        })

//        Toast.makeText(context,"Invoice Added", Toast.LENGTH_SHORT)
//                .show()
    }

    private fun setUpMap() {
        if (ActivityCompat.checkSelfPermission(context!!,
                        android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity as Activity,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
            return
        }

        map.isMyLocationEnabled = true

//        map.mapType = GoogleMap.MAP_TYPE_TERRAIN
//        map.mapType = GoogleMap.MAP_TYPE_NORMAL

        fusedLocationClient.lastLocation.addOnSuccessListener(activity as Activity) { location ->

            if (location != null) {
                lastLocation = location
                val currentLatLng = LatLng(location.latitude, location.longitude)
//                placeMarkerOnMap(currentLatLng)
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 8f))
            }
        }

    }
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

//        val sydney = LatLng(-34.0, 151.0)
//        map.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
//        map.moveCamera(CameraUpdateFactory.newLatLng(sydney))

//        val myPlace = LatLng(40.73, -73.99)  // this is New York
//        map.addMarker(MarkerOptions().position(myPlace).title("My Favorite City"))
//        map.moveCamera(CameraUpdateFactory.newLatLng(myPlace))
//        map.moveCamera(CameraUpdateFactory.newLatLngZoom(myPlace, 12.0f))


        map.getUiSettings().setZoomControlsEnabled(true)
        map.setOnMarkerClickListener(this)

        setUpMap()
    }


    private fun placeMarkerOnMap(location: LatLng) {
        // 1
        val markerOptions = MarkerOptions().position(location)

        val titleStr = getAddress(location)  // add these two lines
        markerOptions.title(titleStr)

//        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))

//        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(
//                BitmapFactory.decodeResource(resources, R.mipmap.ic_user_location)))

        map.addMarker(markerOptions)
    }

    private fun placeDirectionMarkerOnMap(location: LatLng, title:String) {
        // 1
        val markerOptions = MarkerOptions().position(location)
        markerOptions.title(title)
//        val titleStr = getAddress(location)  // add these two lines
//        markerOptions.title(titleStr)

//        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))

//        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(
//                BitmapFactory.decodeResource(resources, R.mipmap.ic_user_location)))

        map.addMarker(markerOptions)
    }
    override fun onMarkerClick(p0: Marker?): Boolean {
        return false
    }
    private fun getLatLong(direction: Direction): Address? {
        // 1
        val geocoder = Geocoder(context)
        val addresses: List<Address>?
        var address: Address? = null

        try {
            // 2
            addresses = geocoder.getFromLocationName(direction.getAddressString(), 1)
//            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            // 3
            Log.d("DIRECTIONS", addresses.toString())

            if (null != addresses && !addresses.isEmpty()) {
                address = addresses[0]
//                addressText+=address.getAddressLine(0)
////                for (i in 0 until address.maxAddressLineIndex) {
////                    addressText += if (i == 0) address.getAddressLine(i) else "\n" + address.getAddressLine(i)
////                }
            }
        } catch (e: IOException) {
            Log.e("MapsActivity", e.localizedMessage)
        }

        return address
    }

    private fun getAddress(latLng: LatLng): String {
        // 1
        val geocoder = Geocoder(context)
        val addresses: List<Address>?
        val address: Address?
        var addressText = ""

        try {
            // 2
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            // 3
            if (null != addresses && !addresses.isEmpty()) {
                address = addresses[0]
                addressText+=address.getAddressLine(0)
//                for (i in 0 until address.maxAddressLineIndex) {
//                    addressText += if (i == 0) address.getAddressLine(i) else "\n" + address.getAddressLine(i)
//                }
            }
        } catch (e: IOException) {
            Log.e("MapsActivity", e.localizedMessage)
        }

        return addressText
    }

    private fun startLocationUpdates() {
        //1
        if (ActivityCompat.checkSelfPermission(context!!,
                        android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity as Activity,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    LOCATION_PERMISSION_REQUEST_CODE)
            return
        }
        //2
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null /* Looper */)
    }

    private fun createLocationRequest() {

        locationRequest = LocationRequest()
        locationRequest.interval = 10000
        locationRequest.fastestInterval = 5000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        val builder = LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)

        // 4
        val client = LocationServices.getSettingsClient(activity as Activity)
        val task = client.checkLocationSettings(builder.build())

        // 5
        task.addOnSuccessListener {
            locationUpdateState = true
            startLocationUpdates()
        }
        task.addOnFailureListener { e ->
            // 6
            if (e is ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    e.startResolutionForResult(activity as Activity,
                            REQUEST_CHECK_SETTINGS)
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }
            }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode == Activity.RESULT_OK) {
                locationUpdateState = true
                startLocationUpdates()
            }
        }
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                val place = PlacePicker.getPlace(context, data)
                var addressText = place.name.toString()
                addressText += "\n" + place.address.toString()

//                placeMarkerOnMap(place.latLng)
            }
        }
    }

    // 2
    override fun onPause() {
        super.onPause()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }


    public override fun onResume() {
        super.onResume()
        if (!locationUpdateState) {
            startLocationUpdates()
        }
    }

    private fun loadPlacePicker() {
        val builder = PlacePicker.IntentBuilder()

        try {
            startActivityForResult(builder.build(activity as Activity), PLACE_PICKER_REQUEST)
        } catch (e: GooglePlayServicesRepairableException) {
            e.printStackTrace()
        } catch (e: GooglePlayServicesNotAvailableException) {
            e.printStackTrace()
        }
    }

}
//https://www.raywenderlich.com/230-introduction-to-google-maps-api-for-android-with-kotlin

