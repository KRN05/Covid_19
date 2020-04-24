package com.krnventures.covid_19_2

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.krnventures.covid_19_2.dto.TravelHistoryListDTO
import com.krnventures.covid_19_2.dto.Travel_HistoryDTO
import com.krnventures.covid_19_2.network.ApiInterface
import kotlinx.android.synthetic.main.activity_location.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.min


class StatsActivity : AppCompatActivity() {


    private var mTravelLocations: MutableList<Travel_HistoryDTO> = ArrayList()
    private var minDistance: Double = 1000000000.0
    private var PERMISSION_ID = 1
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    var mylat: Double = 0.0
    var mylung: Double = 0.0
    lateinit var strList1: List<String>
    var destinationLat: Double = 0.0
    var destinationLong: Double = 0.0
    private var locationName: String = " "


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        getLastLocation()

        apiCall()
    }

    fun apiCall() {
        Log.i("timed", "2")

        val apiInterface = ApiInterface.create().getTravelHistory()
        apiInterface.enqueue(
            object : Callback<TravelHistoryListDTO> {
                override fun onFailure(call: Call<TravelHistoryListDTO>, t: Throwable) {
                    Log.i("hithere2", "failure")
                }

                override fun onResponse(
                    call: Call<TravelHistoryListDTO>,
                    response: Response<TravelHistoryListDTO>
                ) {
                    val travleLocations = response.body()
                    Log.i("hithere", travleLocations.toString())

                    mTravelLocations.addAll(travleLocations!!.travel_history!!)
                    calculateDistancePart(mTravelLocations)

                }

            }
        )
    }

    fun calculateDistancePart(mmTravel: MutableList<Travel_HistoryDTO>) {
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
        minDistance = Math.round(minDistance * 100.0) / 100.0
        txt_location.text = locationName
        txt_distance.text =
            String.format(resources.getString(R.string.location_distance), minDistance.toString());

        val geoUri =
            "http://maps.google.com/maps?q=loc:" + destinationLat.toString() + "," + destinationLong.toString() + " (" + locationName + ")"
        button3.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(geoUri))
            startActivity(intent)
        }
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
}






