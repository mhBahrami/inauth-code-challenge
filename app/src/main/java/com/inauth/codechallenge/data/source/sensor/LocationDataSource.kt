package com.inauth.codechallenge.data.source.sensor

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import androidx.annotation.VisibleForTesting
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.tasks.OnSuccessListener
import com.inauth.codechallenge.data.source.AppDataSource
import com.inauth.codechallenge.util.AppExecutors

class LocationDataSource private constructor (
    private val appExecutors: AppExecutors,
    private val fusedLocationProviderClient: FusedLocationProviderClient
) : AppDataSource.LocationDataSource {

    private val centerOfSanFransisco =
        Location("CENTER_OF_SAN_FRANSISCO").apply{
            this.latitude = 37.754362
            this.longitude = -122.447141
        }

    @SuppressLint("MissingPermission")
    override fun onGetLastLocationInfo(callback: AppDataSource.LoadLocationInfoCallback) {
        appExecutors.sensorIO.execute {
            fusedLocationProviderClient.lastLocation
                .addOnSuccessListener(appExecutors.mainThread,
                    OnSuccessListener<Location> { location ->
                        // Got last known location. In some rare situations this can be null.
                        if (location == null) {
                             val locationManager =
                                 fusedLocationProviderClient.applicationContext.
                                     getSystemService( Context.LOCATION_SERVICE ) as LocationManager

                            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                                callback.onTurnedOffLocation()
                            }
                            else callback.onUnableToGetLocation()
                        } else {
                            val locationString = "Latitude: ${location.latitude}\nLongitude: ${location.longitude}"
                            val distInKm = (location.distanceTo(centerOfSanFransisco) / 1000).toInt()
                            val distInMile = (distInKm * 0.621371).toInt()
                            val distInKmString = "The distance is ${distInKm}km (${distInMile}miles)."

                            callback.onLoadedLocationInfo(locationString, distInKmString)
                        }
                    })
        }
    }

    companion object {
        private var INSTANCE: LocationDataSource? = null

        @JvmStatic
        fun getInstance(appExecutors: AppExecutors,
                        fusedLocationProviderClient: FusedLocationProviderClient): LocationDataSource {
            if (INSTANCE == null) {
                synchronized(LocationDataSource::javaClass) {
                    INSTANCE = LocationDataSource(appExecutors, fusedLocationProviderClient)
                }
            }
            return INSTANCE!!
        }

        @VisibleForTesting
        fun clearInstance() {
            INSTANCE = null
        }
    }
}
