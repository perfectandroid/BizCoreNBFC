package com.perfect.nbfc.locations

import android.Manifest
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import java.io.IOException
import java.util.*


public class Locations : Service(), LocationListener,
    GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener {

    private val TAG = "LocationServicess"
    private var mGoogleApiClient: GoogleApiClient? = null
    private var mLocationRequest: LocationRequest? = null

    private val INTERVAL = 100 * 1 * 60.toLong()
    private val FASTEST_INTERVAL = 100 * 1 * 6.toLong()
    private val MEDIUM_INTERVAL = 100 * 1 * 6.toLong()
    var addresses: List<Address>? = null

    val MyPREFERENCES = "MyBizcore"
    var sharedpreferences: SharedPreferences? = null
    var editor: SharedPreferences.Editor?=null

    var i = 0

    override fun onCreate() {
        super.onCreate()
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE)
        editor = sharedpreferences!!.edit()
        createLocationRequest()
        mGoogleApiClient = GoogleApiClient.Builder(this)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API)
            .build()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (!mGoogleApiClient!!.isConnected) {
            mGoogleApiClient!!.connect()
        } else {
            startLocationUpdates()
        }
        return START_STICKY
    }


    override fun onBind(intent: Intent?): IBinder? {
        // TODO: Return the communication channel to the service.
        throw UnsupportedOperationException("Not yet implemented")
    }

    protected fun createLocationRequest() {
        mLocationRequest = LocationRequest()
        mLocationRequest!!.setInterval(MEDIUM_INTERVAL)
        mLocationRequest!!.setFastestInterval(FASTEST_INTERVAL)
        mLocationRequest!!.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
    }

    override fun onConnected(bundle: Bundle?) {
        Log.e(
            TAG,
            "onConnected - isConnected ...............: " + mGoogleApiClient!!.isConnected
        )
        if (mGoogleApiClient!!.isConnected) {
            startLocationUpdates()
        }
    }

    protected fun startLocationUpdates() {
        if (mGoogleApiClient != null) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient,
                mLocationRequest,
                this
            )
            Log.v(TAG, "Location update started ..............: ")
        }
    }

    override fun onConnectionSuspended(i: Int) {}



    override fun onLocationChanged(location: Location) {
        Log.e(
            TAG,
            "Latitude: " + location.latitude + " Longitude: " + location.longitude
        )
        getLocationAddress(location.latitude, location.longitude)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopLocationUpdates()
        Log.v(TAG, "Service Stopped!")
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        TODO("Not yet implemented")
    }

    protected fun stopLocationUpdates() {
        if (mGoogleApiClient != null && mGoogleApiClient!!.isConnected && LocationServices.FusedLocationApi != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this
            )
            Log.v(TAG, "Location update stopped .......................")
            mGoogleApiClient!!.disconnect()
        }
    }

    private fun getLocationAddress(
        currentLatitude: Double,
        currentLongitude: Double
    ) {
        val geocoder: Geocoder
        geocoder = Geocoder(this, Locale.getDefault())
        try {
            addresses = geocoder.getFromLocation(
                currentLatitude,
                currentLongitude,
                1
            ) // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            setLocationAddress()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun setLocationAddress() {
        if (addresses != null) {

            Log.e(TAG,"LOCATION_SERVICE    "+addresses!![0].getAddressLine(0)+"  "+addresses!![0].latitude.toString())
            editor!!.putString("Latitude", addresses!![0].latitude.toString())
            editor!!.putString("Longitude", addresses!![0].longitude.toString())
            editor!!.putString("AddressLine", addresses!![0].getAddressLine(0))
            editor!!.putString("Locality", addresses!![0].locality)
            editor!!.putString("Area", addresses!![0].subLocality)
            editor!!.putString("Country", addresses!![0].countryName)
            editor!!.putString("PostalCode", addresses!![0].postalCode)
            editor!!.commit()

        }
    }
}