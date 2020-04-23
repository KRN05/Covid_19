package com.krnventures.covid_19_2

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.krnventures.covid_19_2.dto.TravelHistoryListDTO
import com.krnventures.covid_19_2.dto.Travel_HistoryDTO
import com.krnventures.covid_19_2.network.ApiInterface
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.collections.ArrayList
import kotlin.coroutines.resumeWithException
import kotlin.math.min

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {


    private lateinit var mMap: GoogleMap
    private var mTravelLocations: MutableList<Travel_HistoryDTO> = ArrayList()
    private var minDistance: Double = 1000000000.0
    private var PERMISSION_ID = 1
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    var mylat: Double = 0.0
    var mylung: Double = 0.0
    lateinit var strList1: List<String>
    var googleMap: GoogleMap? = null
    var destinationLat: Double = 0.0
    var destinationLong: Double = 0.0
    private var locationName: String = " "


    private val scope = CoroutineScope(Dispatchers.Default)
    private lateinit var fetchJob: Job


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        getLastLocation()

      //  apiCall(this)

        fetchJob = scope.launch {
            var result : TravelHistoryListDTO? = null
            try {
                result = apiCall()

            } catch (t: Throwable) {
                // do sth with error
            }
            result?.travel_history
                .let { it?.let { it1 -> calculateDistancePart(it1) } }
                .apply { mTravelLocations = result?.travel_history!! }

        }
    }

    private suspend fun apiCall() = suspendCancellableCoroutine<TravelHistoryListDTO> { cont ->
        ApiInterface.create().getTravelHistory().enqueue(
            object : Callback<TravelHistoryListDTO> {
                override fun onFailure(call: Call<TravelHistoryListDTO>, t: Throwable) {
                    Log.i("hithere2", "failure")
                    cont.resumeWithException(t)
                }

                override fun onResponse(call: Call<TravelHistoryListDTO>, response: Response<TravelHistoryListDTO>) {
                    cont.resumeWith(response.body())
                }
            }
        )
    }

    private fun calculateDistancePart(mmTravel: MutableList<Travel_HistoryDTO>) {
        for (items in mmTravel) {
            val siz: Int = items.latlong.toString().length

            if (items._cn6ca == "15") {
                continue
            }
            for (x in 0 until siz) {
                //trim lat lung
                strList1 = items.latlong.toString().split(',').map { it.trim() }

            }
            if (strList1[0].isEmpty() || strList1[1].isEmpty()) {
                continue
            }
            Log.i(
                "kyahua",
                items._cn6ca.toString() + " " + items.latlong.toString() + " " + strList1[0] + " " + strList1[1]
            )

            val thisDistance: Double = DistanceCalculator(
                mylat,
                mylung,
                strList1[0].toDouble(),
                strList1[1].toDouble(),
                "K"
            )
            Log.i("calculatedDist", thisDistance.toString())
            if (thisDistance < minDistance) {
                Log.i("hahai", thisDistance.toString())
                minDistance = min(minDistance, thisDistance)
                destinationLat = strList1[0].toDouble()
                destinationLong = strList1[1].toDouble()
                locationName = items.address.toString()
            }
        }
        Log.i("minDistance", minDistance.toString())
        Log.i(
            "minLatLung",
            destinationLat.toString() + " " + destinationLong.toString()
        )
    }


    private fun DistanceCalculator(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double,
        unit: String
    ): Double {
        return if (lat1 == lat2 && lon1 == lon2) {
            0.0
        } else {
            val theta = lon1 - lon2
            var dist =
                Math.sin(Math.toRadians(lat1)) * Math.sin(
                    Math.toRadians(lat2)
                ) + Math.cos(Math.toRadians(lat1)) * Math.cos(
                    Math.toRadians(
                        lat2
                    )
                ) * Math.cos(Math.toRadians(theta))
            dist = Math.acos(dist)
            dist = Math.toDegrees(dist)
            dist = dist * 60 * 1.1515
            if (unit == "K") {
                dist = dist * 1.609344
            } else if (unit == "N") {
                dist = dist * 0.8684
            }
            dist
        }
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        Log.i("bhaiproblem", "$destinationLat $destinationLong")
        scope.launch {
            fetchJob.join() // suspends the coroutine, does not block thread!
            // use your variables as you wish here
            // every variable is set because fetchJob has completed
            val sydney = LatLng(destinationLat, destinationLong)
            googleMap?.addMarker(MarkerOptions().position(sydney).title(locationName))
            googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 15f))
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        Log.i("timed", "1")
        if (checkPermissions()) {
            if (isLocationEnabled()) {

                fusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                    val location: Location? = task.result
                    if (location != null) {
                        mylat = location.latitude.toDouble()
                        mylung = location.longitude.toDouble()
                        Log.i("myLocation", mylat.toString() + " " + mylung.toString())
                    }
                }
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show()
            }
        } else {
            requestPermissions()
        }
    }

    //map permission code
    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            PERMISSION_ID
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_ID) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLastLocation()
            }
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun <T> CancellableContinuation<T>.resumeWith(result: TravelHistoryListDTO?) {
        Log.i("haha", result.toString())
        result?.travel_history?.let { calculateDistancePart(it) }
    }
}





